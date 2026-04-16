package com.example.criapp.data.remote

import android.content.Intent
import com.example.criapp.SignInActivity
import com.example.criapp.data.local.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefsManager: PreferencesManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", prefsManager.getAuthorizationHeader())
            .build()
        val response = chain.proceed(request)
        if (response.code == 401) {
            prefsManager.clearAll()
            val intent = Intent(prefsManager.applicationContext, SignInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            prefsManager.applicationContext.startActivity(intent)
        }
        return response
    }
}
