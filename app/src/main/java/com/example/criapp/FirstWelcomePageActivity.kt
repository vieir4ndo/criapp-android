package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class FirstWelcomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_welcome_page)
    }

    fun onArrowButtonClick(view: View?) {
        val nextIntent = Intent(this, SecondWelcomePageActivity::class.java);
        startActivity(nextIntent);
    }

}