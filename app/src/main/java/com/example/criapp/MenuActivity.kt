package com.example.criapp

import android.content.Context
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

    fun onButtonFaqClick(view: View?){
        val nextIntent = Intent(this, FaqActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonLogoffClick(view: View?) {
        val sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.clear();

        val nextIntent = Intent(this, SignInActivity::class.java);
        startActivity(nextIntent);
    }
}