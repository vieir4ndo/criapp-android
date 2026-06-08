package com.example.criapp.data.remote

import android.content.Intent
import com.example.criapp.SignInActivity
import com.example.criapp.data.local.PreferencesManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.Locale

class AuthInterceptor(
    private val prefsManager: PreferencesManager,
    private val baseUrl: String
) : Interceptor {

    private val refreshClient = OkHttpClient()
    private val gson = Gson()
    private val lock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val locale = Locale.getDefault().language
        val request = chain.request().newBuilder()
            .addHeader("Authorization", prefsManager.getAuthorizationHeader())
            .addHeader("Accept-Language", locale)
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            response.close()
            val refreshed = synchronized(lock) {
                val refreshToken = prefsManager.getRefreshToken()
                if (refreshToken.isEmpty() || prefsManager.isRefreshTokenExpired()) {
                    null
                } else {
                    tryRefreshToken(refreshToken)?.also { tokens ->
                        prefsManager.updateTokens(
                            tokens.token,
                            tokens.expiresIn,
                            tokens.refreshToken,
                            tokens.refreshTokenExpiresIn
                        )
                    }
                }
            }

            if (refreshed != null) {
                val retryRequest = request.newBuilder()
                    .header("Authorization", "Bearer ${refreshed.token}")
                    .build()
                return chain.proceed(retryRequest)
            }

            prefsManager.clearAll()
            redirectToLogin()
        }

        return response
    }

    private fun tryRefreshToken(refreshToken: String): TokenData? {
        return try {
            val body = JSONObject().put("refreshToken", refreshToken)
                .toString()
                .toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("${baseUrl}auth/refresh-token")
                .post(body)
                .build()

            val response = refreshClient.newCall(request).execute()
            if (!response.isSuccessful) return null

            val responseBody = response.body?.use { it.string() } ?: return null
            val type = object : TypeToken<ApiResponse<List<RefreshTokenData>>>() {}.type
            val parsed: ApiResponse<List<RefreshTokenData>> = gson.fromJson(responseBody, type)

            if (!parsed.success) return null
            val data = parsed.data?.firstOrNull() ?: return null
            if (data.token.isNullOrEmpty() || data.refreshToken.isNullOrEmpty()) return null

            TokenData(
                token = data.token,
                expiresIn = data.expiresIn ?: return null,
                refreshToken = data.refreshToken,
                refreshTokenExpiresIn = data.refreshTokenExpiresIn ?: return null
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(prefsManager.applicationContext, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        prefsManager.applicationContext.startActivity(intent)
    }

    private data class TokenData(
        val token: String,
        val expiresIn: String,
        val refreshToken: String,
        val refreshTokenExpiresIn: String
    )

    private data class RefreshTokenData(
        val token: String?,
        val expiresIn: String?,
        val refreshToken: String?,
        val refreshTokenExpiresIn: String?
    )
}

