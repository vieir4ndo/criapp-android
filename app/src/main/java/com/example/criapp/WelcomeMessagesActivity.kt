package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager

class WelcomeMessagesActivity : AppCompatActivity() {

    private var welcomeMessages: Array<String>? = null
    private var currentMessage = -1
    private var textView: TextView? = null
    private val prefsManager by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_messages)
        textView = findViewById(R.id.welcome_message)
        welcomeMessages = resources.getStringArray(R.array.welcome_messages)
        showNextMessage()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })
    }

    private fun showNextMessage() {
        currentMessage++
        textView!!.text = welcomeMessages!![currentMessage]
    }

    fun onArrowButtonClick(view: View?) {
        if (currentMessage == welcomeMessages!!.lastIndex) {
            prefsManager.markOnboardingCompleted()
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        } else {
            showNextMessage()
        }
    }
}