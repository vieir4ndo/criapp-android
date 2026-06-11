package com.example.criapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.criapp.BuildConfig
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppApiService
import com.example.criapp.models.requests.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

class CriAppWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return CriAppWidgetItemFactory(applicationContext)
    }
}

private data class WidgetChildItem(
    val name: String,
    val answeredToday: Boolean,
    val score: Int
)

private class CriAppWidgetItemFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var items = listOf<WidgetChildItem>()
    private var isAuthError = false
    private var isNetworkError = false
    private var isBlockedError = false

    private fun buildApi(prefs: PreferencesManager): CriAppApiService {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val locale = Locale.getDefault().language
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", prefs.getAuthorizationHeader())
                    .addHeader("Accept-Language", locale)
                    .build()
                chain.proceed(request)
            })
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CriAppApiService::class.java)
    }

    override fun onCreate() {}

    private suspend fun tryRefreshToken(prefs: PreferencesManager): Boolean {
        return try {
            val refreshToken = prefs.getRefreshToken()
            if (refreshToken.isEmpty()) return false
            val api = buildApi(prefs)
            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            val data = response.data?.firstOrNull() ?: return false
            prefs.updateTokens(data.token, data.expiresIn, data.refreshToken, data.refreshTokenExpiresIn)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isNetworkUnavailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return true
        val caps = cm.getNetworkCapabilities(network) ?: return true
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDataSetChanged() {
        isAuthError = false
        isNetworkError = false
        isBlockedError = false
        items = emptyList()
        try {
            val prefs = PreferencesManager(context)
            if (prefs.isRefreshTokenExpired() || prefs.getToken().isEmpty()) {
                isAuthError = true
                return
            }
            runBlocking {
                try {
                    if (!prefs.isSessionActive() && !tryRefreshToken(prefs)) {
                        isAuthError = true
                        return@runBlocking
                    }
                    val api = buildApi(prefs)
                    val response = api.getChildren()
                    if (!response.success) return@runBlocking
                    items = response.data?.flatten()?.map { child ->
                        val score = try {
                            api.getChildBookScore(child.id).data?.firstOrNull()?.score ?: 0
                        } catch (e: Exception) { 0 }
                        WidgetChildItem(child.name, child.answeredToday, score)
                    } ?: emptyList()
                } catch (e: HttpException) {
                    if (e.code() == 401 && tryRefreshToken(prefs)) {
                        try {
                            val freshApi = buildApi(prefs)
                            val response = freshApi.getChildren()
                            if (response.success) {
                                items = response.data?.flatten()?.map { child ->
                                    val score = try {
                                        freshApi.getChildBookScore(child.id).data?.firstOrNull()?.score ?: 0
                                    } catch (e2: Exception) { 0 }
                                    WidgetChildItem(child.name, child.answeredToday, score)
                                } ?: emptyList()
                            }
                        } catch (e2: Exception) {
                            isAuthError = true
                        }
                    } else if (e.code() == 401) {
                        isAuthError = true
                    }
                } catch (e: IOException) {
                    if (isNetworkUnavailable()) {
                        isNetworkError = true
                    } else {
                        isBlockedError = true
                    }
                } catch (e: Exception) {
                    isBlockedError = true
                }
            }
        } catch (e: Exception) {
            items = emptyList()
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = if (isAuthError || isNetworkError || isBlockedError) 1 else items.size

    override fun getViewAt(position: Int): RemoteViews {
        if (isAuthError || isNetworkError || isBlockedError) {
            val rv = RemoteViews(context.packageName, R.layout.widget_auth_error_item)
            val msg = when {
                isNetworkError -> R.string.widget_no_internet
                isBlockedError -> R.string.widget_tap_to_refresh
                else -> R.string.widget_not_logged_in
            }
            rv.setTextViewText(R.id.widget_auth_message, context.getString(msg))
            rv.setOnClickFillInIntent(R.id.widget_item_root, Intent())
            return rv
        }
        val item = items.getOrNull(position) ?: return RemoteViews(context.packageName, R.layout.widget_child_item)
        val rv = RemoteViews(context.packageName, R.layout.widget_child_item)
        rv.setTextViewText(R.id.widget_child_name, item.name)
        rv.setTextViewText(R.id.widget_child_score, item.score.toString())
        rv.setImageViewResource(
            R.id.widget_child_heart,
            if (item.answeredToday) R.drawable.widget_heart_filled else R.drawable.widget_heart_outline
        )
        rv.setOnClickFillInIntent(R.id.widget_item_root, Intent())
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 2
    override fun getItemId(position: Int): Long = if (isAuthError || isBlockedError) -1L else position.toLong()
    override fun hasStableIds(): Boolean = true
}
