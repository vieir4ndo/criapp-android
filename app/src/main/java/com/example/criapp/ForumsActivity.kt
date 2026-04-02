package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.criapp.adapters.ForumAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.CriAppHttpClient
import com.example.criapp.data.repository.ForumRepositoryImpl
import com.example.criapp.helpers.ViewFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.forum.ForumViewModel
import com.example.criapp.presentation.forum.ForumViewModelFactory
import com.example.criapp.services.DialogFactory

class ForumsActivity : AppCompatActivity() {

    private val viewModel: ForumViewModel by viewModels {
        ForumViewModelFactory(ForumRepositoryImpl(CriAppHttpClient(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forums)

        aLoadingDialog = ALoadingDialog(this)

        viewModel.forumsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    val view = findViewById<LinearLayout>(R.id.forums)
                    val forums = state.data
                    val userId = PreferencesManager(this).getUserId()

                    if (forums.isEmpty()) {
                        view.addView(ViewFactory.generateEmptyTextMessage(this, resources))
                    } else {
                        val adapter = ForumAdapter(forums.toTypedArray(), userId, ::redirectToView, ::redirectToUpdate)
                        val recyclerView = RecyclerView(this)
                        recyclerView.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter
                        view.addView(recyclerView)
                    }

                    view.addView(ViewFactory.generateAddButton(this, resources, "Añadir foro", CreateForumActivity::class.java))
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message).show()
                }
                else -> Unit
            }
        }

        viewModel.loadForums()
    }

    private fun redirectToUpdate(forum: String) {
        startActivity(Intent(this, UpdateForumActivity::class.java).putExtra("forum", forum))
    }

    private fun redirectToView(forum: String) {
        startActivity(Intent(this, ForumActivity::class.java).putExtra("forum", forum))
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}
