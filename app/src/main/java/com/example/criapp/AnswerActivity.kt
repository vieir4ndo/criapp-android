package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.criapp.model.ChildModel
import com.example.criapp.model.QuestionModel
import com.example.criapp.model.ResponseModel
import com.example.criapp.services.CriAppClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AnswerActivity : AppCompatActivity() {
    private var child : ChildModel? = null;
    private val gson = Gson();
    private var textView : TextView? = null;
    private var textQuestion : TextView? = null;
    private var criAppClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;
    private var questions : Array<QuestionModel>? = null;
    private var currentQuestion = -1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        criAppClient = CriAppClient(getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE));

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);

        child = gson.fromJson(intent.getStringExtra("child"), ChildModel::class.java)

        textView = findViewById(R.id.text_child);
        textQuestion = findViewById(R.id.text_question);
        textView!!.text = "Respondendo preguntas para " + child!!.name;
        fetchData()
    }

    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.getAuthenticated("/question")
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        if (result == null || !result.success) {
            //TODO: Adicionar mensagens de erro
        } else {
            val json = gson.toJson(result!!.data!!);
            questions = gson.fromJson(json, Array<QuestionModel>::class.java);

            showNextQuestion();
        }
    }

    private fun showNextQuestion(){
        currentQuestion++;
        textQuestion!!.text = questions!![currentQuestion].text
    }

    fun onArrowButtonClick(view: View?) {
        if (currentQuestion == questions!!.lastIndex){
            val nextIntent = Intent(this, MenuActivity::class.java);
            startActivity(nextIntent);
        }
        else{
            showNextQuestion();
        }
    }

}