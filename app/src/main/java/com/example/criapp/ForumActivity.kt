package com.example.criapp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.CommentAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.ForumRepositoryImpl
import com.example.criapp.models.CreateCommentModel
import com.example.criapp.models.responses.ForumResponse
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.forum.ForumViewModel
import com.example.criapp.presentation.forum.ForumViewModelFactory
import com.example.criapp.services.DialogFactory
import com.google.gson.Gson

class ForumActivity : AppCompatActivity() {

    private val viewModel: ForumViewModel by viewModels {
        ForumViewModelFactory(ForumRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var forum: ForumResponse
    private lateinit var editTextText: EditText
    private lateinit var errorTextView: TextView
    private lateinit var model: CreateCommentModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        aLoadingDialog = ALoadingDialog(this)
        forum = Gson().fromJson(intent.getStringExtra("forum"), ForumResponse::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.forum_name).text = forum.name
        findViewById<TextView>(R.id.forum_description).text = forum.description
        findViewById<TextView>(R.id.forum_owner).text = getString(R.string.forum_created_by, forum.prcName)

        errorTextView = findViewById(R.id.text_view_errors)
        errorTextView.visibility = View.GONE
        editTextText = findViewById(R.id.text_comment)
        model = CreateCommentModel(editTextText, forum.id)

        findViewById<ImageButton>(R.id.comment_button).setOnClickListener {
            if (model.validate(errorTextView, this)) {
                viewModel.createComment(model.toRequest())
            } else {
                errorTextView.visibility = View.VISIBLE
            }
        }

        viewModel.commentsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    val userId = PreferencesManager(this).getUserId()
                    recyclerView.adapter = CommentAdapter(state.data.toTypedArray(), userId, ::deleteComment)
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.error_load_comments), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.commentActionState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    editTextText.text = null
                    viewModel.loadComments(forum.id)
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }

        viewModel.loadComments(forum.id)
    }

    private fun deleteComment(id: String) {
        viewModel.deleteComment(id)
    }
}
