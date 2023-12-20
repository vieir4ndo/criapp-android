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
        val nextIntent = Intent(this, ChildActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonProfileClick(view: View?) {
        val nextIntent = Intent(this, ProfileActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonChildQuestions(view: View?) {
        val nextIntent = Intent(this, QuestionsActivity::class.java);
        startActivity(nextIntent);
    }
}