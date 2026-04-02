package com.example.criapp

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.ChildRepositoryImpl
import com.example.criapp.data.repository.QuestionRepositoryImpl
import com.example.criapp.helpers.ViewFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.question.QuestionViewModel
import com.example.criapp.presentation.question.QuestionViewModelFactory
import com.example.criapp.services.DialogFactory

class QuestionsActivity : AppCompatActivity() {

    private val viewModel: QuestionViewModel by viewModels {
        val client = CriAppHttpClient(PreferencesManager(this))
        QuestionViewModelFactory(ChildRepositoryImpl(client), QuestionRepositoryImpl(client))
    }
    private var aLoadingDialog: ALoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        aLoadingDialog = ALoadingDialog(this)

        viewModel.childrenState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    val view = findViewById<LinearLayout>(R.id.children)
                    view.removeAllViews()
                    val children = state.data

                    if (children.isEmpty()) {
                        view.addView(ViewFactory.generateEmptyTextMessage(this, resources))
                    }

                    for (child in children) {
                        view.addView(ViewFactory.generateButton(this, resources, "Para " + child.name, child.toString(), AnswerActivity::class.java, "child"))
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadChildren()
    }
}
