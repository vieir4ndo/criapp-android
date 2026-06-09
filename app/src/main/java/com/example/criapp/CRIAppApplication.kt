package com.example.criapp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class CRIAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    companion object {
        lateinit var context: Context
            private set
    }
}
