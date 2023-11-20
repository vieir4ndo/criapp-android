package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class FourthWelcomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fourth_welcome_page)
    }

    fun onArrowButtonClick(view: View?) {
        val nextIntent = Intent(this, FifthWelcomePageActivity::class.java);
        startActivity(nextIntent);
    }
}