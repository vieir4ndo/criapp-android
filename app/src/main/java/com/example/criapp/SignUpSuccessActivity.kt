package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity

class SignUpSuccessActivity : BaseActivity() {

    override val navigationBarColorRes: Int get() = R.color.light_blue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_success)
    }

    fun onButtonClick(view: View?) {
        startActivity(Intent(this, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}