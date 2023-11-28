package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.criapp.model.ChildModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuestionsActivity : AppCompatActivity() {
    private var children : Array<ChildModel>? = null;
    private var criAppClient : CriAppClient? = null;
    private val gson = Gson();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)
        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));
        fetchData();
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.getAuthenticated("/child");
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        if (result == null || !result.success){
            //TODO: Adicionar mensagens de erro
        }
        else {
            val json = gson.toJson(result!!.data!!.first());
            val data = gson.fromJson(json, Array<ChildModel>::class.java);

            children = data;

            val view = findViewById<LinearLayout>(R.id.children);

            for (child in children!!){
                val button = Button(this)
                button.gravity = Gravity.CENTER_HORIZONTAL
                button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
                button.textSize = 20f
                button.setTypeface(null, Typeface.BOLD)
                button.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))

                val shape = GradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadius = resources.getDimension(R.dimen.border_radius)
                shape.setColor(ContextCompat.getColor(this, R.color.white))

                button.background = shape

                button.text = "Para " + child.name

                button.setOnClickListener { view ->
                    val intent = Intent(this, AnswerActivity::class.java);
                    intent.putExtra("child", child.toString())
                    startActivity(intent);
                }

                view.addView(button);
            }
        }
    }
}