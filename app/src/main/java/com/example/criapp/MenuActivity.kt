package com.example.criapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE)
        val name = sharedPreferences!!.getString("user_name", "")
        val greeting = findViewById<TextView>(R.id.greeting)
        greeting!!.text = "¡Hola, " + name + "!"
    }

    fun onButtonChildClick(view: View?) {
        val nextIntent = Intent(this, ChildActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonProfileClick(view: View?) {
        val nextIntent = Intent(this, ProfileActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonQuestionsClick(view: View?) {
        val nextIntent = Intent(this, QuestionsActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonFaqClick(view: View?){
        val nextIntent = Intent(this, FaqActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonAudioVisualResourcesClick(view: View?){
        val nextIntent = Intent(this, AudioVisualResourcesActivity::class.java);
        startActivity(nextIntent);
    }

    fun onButtonForumClick(view: View?){
        val nextIntent = Intent(this, ForumsActivity::class.java);
        startActivity(nextIntent);
    }

    override fun onBackPressed() {
        return
    }
}