package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class FifthWelcomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fifth_welcome_page)
    }

    fun onArrowButtonClick(view: View?) {
        val nextIntent = Intent(this, MenuActivity::class.java);
        startActivity(nextIntent);
    }
}