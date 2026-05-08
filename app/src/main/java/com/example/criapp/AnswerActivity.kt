package com.example.criapp

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
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
        val service = RetrofitProvider.create(PreferencesManager(this))
        QuestionViewModelFactory(ChildRepositoryImpl(service), QuestionRepositoryImpl(service))
    }
    private val gson = Gson()
    private lateinit var child: ChildResponse
    private var questions: List<QuestionResponse> = emptyList()
    private var currentQuestion = -1
    private var value: Int = 3
    private var selectedIndex: Int = -1
    private var textQuestion: TextView? = null
    private var textQuestionTypeMin: TextView? = null
    private var textQuestionTypeMax: TextView? = null
    private var progressIndicator: LinearProgressIndicator? = null
    private var gradientBar: View? = null
    private lateinit var likertButtons: List<TextView>
    private val likertColors: List<Int> by lazy {
        listOf(
            ContextCompat.getColor(this, R.color.likert_1),
            ContextCompat.getColor(this, R.color.likert_2),
            ContextCompat.getColor(this, R.color.likert_3),
            ContextCompat.getColor(this, R.color.likert_4),
            ContextCompat.getColor(this, R.color.likert_5),
            ContextCompat.getColor(this, R.color.likert_6)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)

        child = gson.fromJson(intent.getStringExtra("child"), ChildResponse::class.java)

        findViewById<TextView>(R.id.text_child).text = getString(R.string.answering_questions_for, child.name)
        textQuestion = findViewById(R.id.text_question)
        textQuestionTypeMin = findViewById(R.id.text_min)
        textQuestionTypeMax = findViewById(R.id.text_max)
        progressIndicator = findViewById(R.id.progress_indicator)
        gradientBar = findViewById(R.id.gradient_bar)
        likertButtons = listOf(
            findViewById(R.id.likert_btn_1),
            findViewById(R.id.likert_btn_2),
            findViewById(R.id.likert_btn_3),
            findViewById(R.id.likert_btn_4),
            findViewById(R.id.likert_btn_5),
            findViewById(R.id.likert_btn_6)
        )
        likertButtons.forEachIndexed { index, btn ->
            btn.setOnClickListener {
                value = index + 1
                selectedIndex = index
                val isReversed = currentQuestion >= 0 && questions[currentQuestion].scoringType == 2
                updateLikertButtons(index, isReversed)
            }
        }

        viewModel.questionsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> progressIndicator?.isIndeterminate = true
                is UiState.Success -> {
                    progressIndicator?.isIndeterminate = false
                    questions = state.data
                    if (questions.isEmpty()) {
                        DialogFactory.createSuccessDialog(this, getString(R.string.all_questions_answered_today, child.name)).show()
                    } else {
                        showNextQuestion(animate = false)
                    }
                }
                is UiState.Error -> {
                    progressIndicator?.isIndeterminate = false
                    Toast.makeText(this, getString(R.string.error_load_questions), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.answerState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> Unit
                is UiState.Success -> {
                    if (currentQuestion == questions.lastIndex) {
                        DialogFactory.createSuccessDialog(this, getString(R.string.all_questions_answered_today, child.name)).show()
                    } else {
                        showNextQuestion()
                    }
                }
                is UiState.Error -> {
                    Toast.makeText(this, getString(R.string.error_save_answer), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.loadQuestions(child.id)
    }

    private fun showNextQuestion(animate: Boolean = true) {
        currentQuestion++
        val newText = questions[currentQuestion].text
        progressIndicator?.setProgressCompat((currentQuestion + 1) * 100 / questions.size, true)
        value = 3
        selectedIndex = -1
        val isReversed = questions[currentQuestion].scoringType == 2
        if (questions[currentQuestion].type == 1) {
            textQuestionTypeMin!!.text = getString(R.string.answer_scale_never)
            textQuestionTypeMax!!.text = getString(R.string.answer_scale_always)
        } else {
            textQuestionTypeMin!!.text = getString(R.string.answer_scale_disagree)
            textQuestionTypeMax!!.text = getString(R.string.answer_scale_agree)
        }
        updateLikertButtons(-1, isReversed)

        if (!animate) {
            textQuestion!!.text = newText
            textQuestion!!.alpha = 1f
            return
        }

        textQuestion!!.animate()
            .alpha(0f)
            .translationX(-40f)
            .setDuration(180)
            .withEndAction {
                textQuestion!!.text = newText
                textQuestion!!.translationX = 40f
                textQuestion!!.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(220)
                    .start()
            }.start()
    }

    fun onArrowButtonClick(view: View?) {
        if (currentQuestion < 0) {
            showNextQuestion(animate = false)
        } else {
            if (selectedIndex == -1) {
                textQuestion!!.animate().translationX(12f).setDuration(50)
                    .withEndAction {
                        textQuestion!!.animate().translationX(-12f).setDuration(50)
                            .withEndAction {
                                textQuestion!!.animate().translationX(0f).setDuration(50).start()
                            }.start()
                    }.start()
                return
            }
            val requestBody = CreateAnswerModel(value, questions[currentQuestion].id, child.id).toString()
            viewModel.answerQuestion(requestBody)
        }
    }

    private fun updateLikertButtons(selectedIndex: Int, isReversed: Boolean) {
        val strokePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics).toInt()
        likertButtons.forEachIndexed { index, btn ->
            val colorIndex = if (isReversed) index else (5 - index)
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(likertColors[colorIndex])
                if (index == selectedIndex) {
                    setStroke(strokePx, Color.WHITE)
                }
            }
            btn.background = drawable
        }
        val startColor = if (isReversed) likertColors[0] else likertColors[5]
        val endColor = if (isReversed) likertColors[5] else likertColors[0]
        gradientBar?.background = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(startColor, likertColors[2], endColor)
        ).apply {
            cornerRadius = resources.getDimension(R.dimen.border_radius)
        }
    }
}
