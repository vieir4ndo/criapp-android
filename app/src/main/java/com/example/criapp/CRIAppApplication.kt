package com.example.criapp

import android.app.Application
import android.content.Context

class CRIAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
            private set
    }
}
