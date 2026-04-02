package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SignUpSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_success)
        window.setNavigationBarColor(resources.getColor(R.color.light_blue))
    }

    fun onButtonClick(view: View?) {
        startActivity(Intent(this, SignInActivity::class.java))
    }
}