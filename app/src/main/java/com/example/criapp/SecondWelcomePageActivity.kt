package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SecondWelcomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_welcome_page)
    }

    fun onArrowButtonClick(view: View?) {
        val nextIntent = Intent(this, ThirdWelcomePageActivity::class.java);
        startActivity(nextIntent);
    }
}