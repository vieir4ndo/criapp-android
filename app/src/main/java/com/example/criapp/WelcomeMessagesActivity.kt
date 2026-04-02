package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeMessagesActivity : AppCompatActivity() {

    private var welcomeMessages: Array<String>? = null
    private var currentMessage = -1
    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_messages)
        textView = findViewById(R.id.welcome_message)
        welcomeMessages = resources.getStringArray(R.array.welcome_messages)
        showNextMessage()
    }

    private fun showNextMessage() {
        currentMessage++
        textView!!.text = welcomeMessages!![currentMessage]
    }

    fun onArrowButtonClick(view: View?) {
        if (currentMessage == welcomeMessages!!.lastIndex) {
            startActivity(Intent(this, MenuActivity::class.java))
        } else {
            showNextMessage()
        }
    }
}