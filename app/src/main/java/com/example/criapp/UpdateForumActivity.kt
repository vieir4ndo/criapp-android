package com.example.criapp

import android.content.Intent
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
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.forum.ForumViewModel
import com.example.criapp.presentation.forum.ForumViewModelFactory
import com.google.gson.Gson

class UpdateForumActivity : AppCompatActivity() {

    private val viewModel: ForumViewModel by viewModels {
        ForumViewModelFactory(ForumRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private lateinit var forum: ForumResponse
    private val gson = Gson()
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var model: CreateForumModel
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_forum)

        aLoadingDialog = ALoadingDialog(this)
        forum = gson.fromJson(intent.getStringExtra("forum"), ForumResponse::class.java)

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE

        val editTextName = findViewById<EditText>(R.id.edit_text_name)
        val editTextDescription = findViewById<EditText>(R.id.edit_text_description)

        editTextName.setText(forum.name)
        editTextDescription.setText(forum.description)

        model = CreateForumModel(editTextName, editTextDescription)

        findViewById<Button>(R.id.button_update).setOnClickListener {
            if (model.validate(errorTextView)) {
                viewModel.updateForum(forum.id, model.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.updateForumState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, "Foro actualizado con exito", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ForumsActivity::class.java))
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
