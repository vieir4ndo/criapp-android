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
import java.util.Locale

class AuthInterceptor(
    private val prefsManager: PreferencesManager,
    private val baseUrl: String
) : Interceptor {

    private val refreshClient = OkHttpClient()
    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        val locale = Locale.getDefault().language
        val request = chain.request().newBuilder()
            .addHeader("Authorization", prefsManager.getAuthorizationHeader())
            .addHeader("Accept-Language", locale)
            .build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            val refreshToken = prefsManager.getRefreshToken()
            if (refreshToken.isNotEmpty()) {
                response.close()
                val newTokens = tryRefreshToken(refreshToken)
                if (newTokens != null) {
                    prefsManager.updateTokens(
                        newTokens.token,
                        newTokens.expiresIn,
                        newTokens.refreshToken,
                        newTokens.refreshTokenExpiresIn
                    )
                    val retryRequest = request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.token}")
                        .build()
                    return chain.proceed(retryRequest)
                }
            }
            response.close()
            prefsManager.clearAll()
            redirectToLogin()
        }

        return response
    }

    @Synchronized
    private fun tryRefreshToken(refreshToken: String): TokenData? {
        return try {
            val json = """{"refreshToken":"$refreshToken"}"""
            val body = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("${baseUrl}auth/refresh-token")
                .post(body)
                .build()

            val response = refreshClient.newCall(request).execute()
            if (!response.isSuccessful) return null

            val responseBody = response.body?.string() ?: return null
            val type = object : TypeToken<ApiResponse<List<Map<String, Any>>>>() {}.type
            val parsed: ApiResponse<List<Map<String, Any>>> = gson.fromJson(responseBody, type)

            if (!parsed.success) return null
            val data = parsed.data?.firstOrNull() ?: return null

            TokenData(
                token = data["token"] as? String ?: return null,
                expiresIn = data["expiresIn"] as? String ?: return null,
                refreshToken = data["refreshToken"] as? String ?: return null,
                refreshTokenExpiresIn = data["refreshTokenExpiresIn"] as? String ?: return null
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
}

