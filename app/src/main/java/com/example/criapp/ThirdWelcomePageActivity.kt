package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ThirdWelcomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_welcome_page)
    }

    fun onArrowButtonClick(view: View?) {
        val nextIntent = Intent(this, FourthWelcomePageActivity::class.java);
        startActivity(nextIntent);
    }
}