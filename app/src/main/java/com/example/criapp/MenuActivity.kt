package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val name = PreferencesManager(this).getUserName().split(" ")[0]
        findViewById<TextView>(R.id.greeting).text = getString(R.string.greeting, name)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { }
        })
    }

    fun onButtonChildClick(view: View?) = startActivity(Intent(this, ChildActivity::class.java))
    fun onButtonProfileClick(view: View?) = startActivity(Intent(this, ProfileActivity::class.java))
    fun onButtonQuestionsClick(view: View?) = startActivity(Intent(this, QuestionsActivity::class.java))
    fun onButtonFaqClick(view: View?) = startActivity(Intent(this, FaqActivity::class.java))
    fun onButtonAudioVisualResourcesClick(view: View?) = startActivity(Intent(this, AudioVisualResourcesActivity::class.java))
    fun onButtonForumClick(view: View?) = startActivity(Intent(this, ForumsActivity::class.java))
    fun onButtonNotificationsClick(view: View?) = Unit
    fun onButtonChildBookClick(view: View?) = startActivity(Intent(this, ChildBookListActivity::class.java))
}