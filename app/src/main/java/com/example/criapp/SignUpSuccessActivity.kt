package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SignUpSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_success)
        window.setNavigationBarColor(getResources().getColor(R.color.light_blue))
    }

    fun onButtonClick(view: View?) {
        val nextIntent = Intent(this, SignInActivity::class.java);
        startActivity(nextIntent);
    }
}