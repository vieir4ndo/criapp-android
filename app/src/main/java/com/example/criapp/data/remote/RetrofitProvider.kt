package com.example.criapp.data.remote

import com.example.criapp.BuildConfig
import com.example.criapp.CRIAppApplication
import com.example.criapp.data.local.PreferencesManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private val instance: CriAppApiService by lazy {
        val prefsManager = PreferencesManager(CRIAppApplication.context)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefsManager, BuildConfig.BASE_URL))
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CriAppApiService::class.java)
    }

    fun create(prefsManager: PreferencesManager): CriAppApiService = instance
}
