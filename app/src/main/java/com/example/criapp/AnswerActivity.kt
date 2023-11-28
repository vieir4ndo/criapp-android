package com.example.criapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.criapp.model.ChildModel
import com.google.gson.Gson

class AnswerActivity : AppCompatActivity() {
    private var child : ChildModel? = null;
    private val gson = Gson();
    private var textView : TextView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        child = gson.fromJson(intent.getStringExtra("child"), ChildModel::class.java)

        textView = findViewById(R.id.text_child);
        textView!!.text = "Respondendo preguntas para " + child!!.name;
    }
}