package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    fun onButtonChildClick(view: View?) {
        val nextIntent = Intent(this, CreateChildActivity::class.java);
        startActivity(nextIntent);
    }
}