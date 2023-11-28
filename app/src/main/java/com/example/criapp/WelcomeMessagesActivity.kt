package com.example.criapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class WelcomeMessagesActivity : AppCompatActivity() {

    private var welcome_messages : Array<String>? = null;
    private var currentMessage = -1;
    private var textView : TextView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_messages);
        textView = findViewById(R.id.welcome_message);
        welcome_messages = resources.getStringArray(R.array.welcome_messages);
        showNextMessage();
    }

    private fun showNextMessage(){
        currentMessage++;
        textView!!.text = welcome_messages!![currentMessage]
    }

    fun onArrowButtonClick(view: View?) {
        if (currentMessage == welcome_messages!!.lastIndex){
            val nextIntent = Intent(this, MenuActivity::class.java);
            startActivity(nextIntent);
        }
        else{
            showNextMessage();
        }
    }
}