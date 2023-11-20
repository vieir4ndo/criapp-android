package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SignUpSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_success)
    }

    fun onButtonClick(view: View?) {
        val nextIntent = Intent(this, SignInActivity::class.java);
        startActivity(nextIntent);
    }
}