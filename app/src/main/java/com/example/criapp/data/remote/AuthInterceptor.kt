package com.example.criapp.data.remote

import com.example.criapp.data.local.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefsManager: PreferencesManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", prefsManager.getAuthorizationHeader())
            .build()
        return chain.proceed(request)
    }
}
