package com.example.criapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.models.CreateAnswerModel
import com.example.criapp.models.responses.QuestionResponse
import com.example.criapp.models.responses.ResponseModel
import com.example.criapp.services.CriAppClient
import com.example.criapp.services.DialogFactory
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AnswerActivity : AppCompatActivity() {
    private var child : ChildResponse? = null;
    private val gson = Gson();
    private var textView : TextView? = null;
    private var textQuestion : TextView? = null;
    private var textQuestionTypeMin : TextView? = null;
    private var textQuestionTypeMax : TextView? = null;
    private var criAppClient : CriAppClient? = null;
    private var sharedPreferences : SharedPreferences? = null;
    private var questions : Array<QuestionResponse>? = null;
    private var currentQuestion = -1;
    private var value : Int = 3;
    private var aLoadingDialog : ALoadingDialog? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        aLoadingDialog = ALoadingDialog(this);
        aLoadingDialog!!.show()

        sharedPreferences = getSharedPreferences("ApiCredentials", Context.MODE_PRIVATE);

        criAppClient = CriAppClient(sharedPreferences!!);

        child = gson.fromJson(intent.getStringExtra("child"), ChildResponse::class.java)

        textView = findViewById(R.id.text_child);
        textQuestion = findViewById(R.id.text_question);
        textQuestionTypeMin = findViewById(R.id.text_min);
        textQuestionTypeMax = findViewById(R.id.text_max);
        textView!!.text = "Respondendo preguntas para " + child!!.name;
        fetchData(child!!.id)
    }

    private fun fetchData(childId: String) {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            criAppClient!!.getAuthenticated("/question?childId=$childId")
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result == null || !result.success) {
            Toast.makeText(this, "Hubo un error al buscar las preguntas, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
        } else {
            val json = gson.toJson(result!!.data!!);
            questions = gson.fromJson(json, Array<QuestionResponse>::class.java);

            if (questions!!.isEmpty()){
                val dialog = DialogFactory.createSuccessDialog(this, "Ya contestaste todas las preguntas de " + child!!.name + " hoy", QuestionsActivity::class.java)
                dialog.show()
            }
            else {
                showNextQuestion();
            }
        }
    }

    private fun showNextQuestion(){
        currentQuestion++;
        textQuestion!!.text = questions!![currentQuestion].text

        if (questions!![currentQuestion].type == 1){
            textQuestionTypeMin!!.text = "Nunca";
            textQuestionTypeMax!!.text = "Siempre";
        }
        else {
            textQuestionTypeMin!!.text = "En desacuerdo";
            textQuestionTypeMax!!.text = "De acuerdo";
        }
    }

    fun onArrowButtonClick(view: View?) {
        if (currentQuestion < 0){
            showNextQuestion();
        }
        else {
            registerAnswer();
        }
    }

    fun onRadioButton1Click(view: View?) {
        value = 1
    }

    fun onRadioButton2Click(view: View?) {
        value = 2
    }

    fun onRadioButton3Click(view: View?) {
        value = 3
    }

    fun onRadioButton4Click(view: View?) {
        value = 4
    }

    fun onRadioButton5Click(view: View?) {
        value = 5
    }

    override fun onBackPressed() {
        val nextIntent = Intent(this, QuestionsActivity::class.java);
        startActivity(nextIntent);
    }

    private fun registerAnswer(){
        aLoadingDialog!!.show()
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            var requestBody = CreateAnswerModel(value, questions!![currentQuestion].id, child!!.id);

            criAppClient!!.postAuthenticated("/answer", requestBody.toString())
        }

        scope.launch {
            val result = job.await()

            handleResultAnswer(result)
        }
    }

    private fun handleResultAnswer(result: ResponseModel?) {
        aLoadingDialog!!.cancel()
        if (result == null || !result.success) {
            println(result!!.Messages!!.joinToString(separator = "\n"));
            Toast.makeText(this, "Hubo un error al guardar tu respuesta, inténtalo nuevamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Respuesta guardada con exito", Toast.LENGTH_SHORT).show()
            if (currentQuestion == questions!!.lastIndex){
                val dialog = DialogFactory.createSuccessDialog(this, "Ya contestaste todas las preguntas de " + child!!.name + " hoy", QuestionsActivity::class.java)
                dialog.show()
            }
            else {
                showNextQuestion();
            }
        }
    }
}