package com.example.criapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.ForumRepositoryImpl
import com.example.criapp.models.CreateForumModel
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.forum.ForumViewModel
import com.example.criapp.presentation.forum.ForumViewModelFactory

class CreateForumActivity : AppCompatActivity() {

    private val viewModel: ForumViewModel by viewModels {
        ForumViewModelFactory(ForumRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: CreateForumModel
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_forum)

        aLoadingDialog = ALoadingDialog(this)

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE

        model = CreateForumModel(
            findViewById(R.id.edit_text_name),
            findViewById(R.id.edit_text_description)
        )

        findViewById<Button>(R.id.button_create).setOnClickListener {
            if (model.validate(errorTextView)) {
                viewModel.createForum(model.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.createForumState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Foro creado con exito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Hubo un error al guardar el foro, por favor intentalo nuevamente", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }
}
