package com.example.criapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import com.example.criapp.base.BaseActivity
import com.example.criapp.data.local.PreferencesManager

class DevToolsActivity : BaseActivity() {

    private var aLoadingDialog: ALoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev_tools)

        aLoadingDialog = ALoadingDialog(this)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnShowLoader).setOnClickListener {
            aLoadingDialog?.show()
            Handler(Looper.getMainLooper()).postDelayed({
                aLoadingDialog?.cancel()
            }, 3000)
        }

        findViewById<View>(R.id.btnBannerSuccess).setOnClickListener {
            showSuccessSnackbar("Success banner de teste")
        }

        findViewById<View>(R.id.btnBannerError).setOnClickListener {
            showErrorSnackbar("Error banner de teste")
        }

        findViewById<View>(R.id.btnBannerInfo).setOnClickListener {
            showInfoSnackbar("Info banner de teste")
        }

        renderPreferences()
    }

    private fun renderPreferences() {
        val prefs = PreferencesManager(this)
        val sb = StringBuilder()
        val userId = prefs.getUserId()
        sb.appendLine("userId        = ${userId}")
        sb.appendLine("userName      = ${prefs.getUserName()}")
        sb.appendLine("isAdmin       = ${prefs.isAdmin()}")
        sb.appendLine("session       = ${prefs.isSessionActive()}")
        sb.appendLine("refreshExpired = ${prefs.isRefreshTokenExpired()}")
        sb.appendLine("onboarding    = ${if (userId.isNotEmpty()) prefs.hasCompletedOnboarding(userId) else "n/a"}")
        sb.appendLine("Access Token expires  = ${prefs.getTokenExpiresIn()}")
        sb.appendLine("Refresh Token expires  = ${prefs.getRefreshTokenExpiresIn()}")
        findViewById<TextView>(R.id.tvPrefs).text = sb.toString().trimEnd()
    }
}
