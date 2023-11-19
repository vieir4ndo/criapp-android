package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val buttonRedirectSignUpActivity = findViewById<Button>(R.id.redirect_sign_up_button);

        buttonRedirectSignUpActivity.setOnClickListener{
            val openSignUpActivityIntent = Intent (this, SignUpActivity::class.java);
            startActivity(openSignUpActivityIntent);
        }
    }
}