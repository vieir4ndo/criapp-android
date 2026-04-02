package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.ChildRepositoryImpl
import com.example.criapp.data.repository.QuestionRepositoryImpl
import com.example.criapp.models.CreateAnswerModel
import com.example.criapp.models.responses.ChildResponse
import com.example.criapp.models.responses.QuestionResponse
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.question.QuestionViewModel
import com.example.criapp.presentation.question.QuestionViewModelFactory
import com.example.criapp.services.DialogFactory
import com.google.gson.Gson

class AnswerActivity : AppCompatActivity() {

    private val viewModel: QuestionViewModel by viewModels {
        val client = CriAppHttpClient(PreferencesManager(this))
        QuestionViewModelFactory(ChildRepositoryImpl(client), QuestionRepositoryImpl(client))
    }
    private val gson = Gson()
    private lateinit var child: ChildResponse
    private var questions: List<QuestionResponse> = emptyList()
    private var currentQuestion = -1
    private var value: Int = 3
    private var aLoadingDialog: ALoadingDialog? = null
    private var textQuestion: TextView? = null
    private var textQuestionTypeMin: TextView? = null
    private var textQuestionTypeMax: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        aLoadingDialog = ALoadingDialog(this)

        child = gson.fromJson(intent.getStringExtra("child"), ChildResponse::class.java)

        findViewById<TextView>(R.id.text_child).text = "Respondendo preguntas para " + child.name
        textQuestion = findViewById(R.id.text_question)
        textQuestionTypeMin = findViewById(R.id.text_min)
        textQuestionTypeMax = findViewById(R.id.text_max)

        viewModel.questionsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    questions = state.data
                    if (questions.isEmpty()) {
                        DialogFactory.createSuccessDialog(this, "Ya contestaste todas las preguntas de " + child.name + " hoy", QuestionsActivity::class.java).show()
                    } else {
                        showNextQuestion()
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Hubo un error al buscar las preguntas, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.answerState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Respuesta guardada con exito", Toast.LENGTH_SHORT).show()
                    if (currentQuestion == questions.lastIndex) {
                        DialogFactory.createSuccessDialog(this, "Ya contestaste todas las preguntas de " + child.name + " hoy", QuestionsActivity::class.java).show()
                    } else {
                        showNextQuestion()
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Hubo un error al guardar tu respuesta, inténtalo nuevamente", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.loadQuestions(child.id)
    }

    private fun showNextQuestion() {
        currentQuestion++
        textQuestion!!.text = questions[currentQuestion].text
        if (questions[currentQuestion].type == 1) {
            textQuestionTypeMin!!.text = "Nunca"
            textQuestionTypeMax!!.text = "Siempre"
        } else {
            textQuestionTypeMin!!.text = "En desacuerdo"
            textQuestionTypeMax!!.text = "De acuerdo"
        }
    }

    fun onArrowButtonClick(view: View?) {
        if (currentQuestion < 0) {
            showNextQuestion()
        } else {
            val requestBody = CreateAnswerModel(value, questions[currentQuestion].id, child.id).toString()
            viewModel.answerQuestion(requestBody)
        }
    }

    fun onRadioButton1Click(view: View?) { value = 1 }
    fun onRadioButton2Click(view: View?) { value = 2 }
    fun onRadioButton3Click(view: View?) { value = 3 }
    fun onRadioButton4Click(view: View?) { value = 4 }
    fun onRadioButton5Click(view: View?) { value = 5 }

    override fun onBackPressed() {
        startActivity(Intent(this, QuestionsActivity::class.java))
    }
}
