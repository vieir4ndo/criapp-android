package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.google.android.material.progressindicator.LinearProgressIndicator

class WelcomeMessagesActivity : AppCompatActivity() {

    private var welcomeMessages: Array<String>? = null
    private var currentMessage = -1
    private var isAnimating = false
    private var textView: TextView? = null
    private var textStep: TextView? = null
    private var progressIndicator: LinearProgressIndicator? = null
    private val prefsManager by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_messages)
        textView = findViewById(R.id.welcome_message)
        textStep = findViewById(R.id.text_step)
        progressIndicator = findViewById(R.id.progress_indicator)
        welcomeMessages = resources.getStringArray(R.array.welcome_messages)
        showNextMessage(animate = false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })
    }

    private fun showNextMessage(animate: Boolean = true) {
        if (animate && isAnimating) return
        currentMessage++
        val total = welcomeMessages!!.size
        val newText = welcomeMessages!![currentMessage]
        val progress = ((currentMessage + 1) * 100 / total)

        textStep!!.text = "${currentMessage + 1} / $total"
        progressIndicator!!.setProgressCompat(progress, animate)

        if (!animate) {
            textView!!.text = newText
            textView!!.alpha = 1f
            textView!!.translationX = 0f
            return
        }

        isAnimating = true
        textView!!.animate()
            .alpha(0f)
            .translationX(-40f)
            .setDuration(180)
            .withEndAction {
                textView!!.text = newText
                textView!!.translationX = 40f
                textView!!.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(220)
                    .withEndAction { isAnimating = false }
                    .start()
            }.start()
    }

    fun onArrowButtonClick(view: View?) {
        if (currentMessage == welcomeMessages!!.lastIndex) {
            prefsManager.markOnboardingCompleted(prefsManager.getUserId())
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        } else {
            showNextMessage()
        }
    }
}