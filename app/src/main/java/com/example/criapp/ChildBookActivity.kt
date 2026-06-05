package com.example.criapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.ChildBookRepositoryImpl
import com.example.criapp.data.repository.ChildRepositoryImpl
import com.example.criapp.presentation.child.ChildViewModel
import com.example.criapp.presentation.child.ChildViewModelFactory
import com.example.criapp.presentation.childbook.ChildBookViewModel
import com.example.criapp.presentation.childbook.ChildBookViewModelFactory
import com.example.criapp.presentation.common.UiState
import com.example.criapp.services.DialogFactory

class ChildBookActivity : BaseActivity() {

    private val childViewModel: ChildViewModel by viewModels {
        ChildViewModelFactory(ChildRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }

    private val childBookViewModel: ChildBookViewModel by viewModels {
        ChildBookViewModelFactory(ChildBookRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_book)

        val childId = intent.getStringExtra("childId") ?: return finish()

        childViewModel.childrenState.observe(this) { state ->
            if (state is UiState.Success) {
                val child = state.data.firstOrNull { it.id == childId }
                if (child != null) {
                    findViewById<TextView>(R.id.childName).text = this.getString(R.string.book_of, child.name)
                }
            }
        }

        childBookViewModel.childBookState.observe(this) { state ->
            if (state is UiState.Success) {
                val score = state.data?.score ?: 0
                findViewById<TextView>(R.id.scoreValue).text = "$score%"
                findViewById<ProgressBar>(R.id.scoreProgressBar).progress = score
            }
        }

        childViewModel.loadChildren()
        childBookViewModel.loadChildBook(childId)

        findViewById<LinearLayout>(R.id.btnAwards).setOnClickListener {
            startActivity(Intent(this, AwardActivity::class.java).putExtra("childId", childId))
        }

        findViewById<LinearLayout>(R.id.btnRecommendations).setOnClickListener {
            startActivity(Intent(this, RecommendationActivity::class.java).putExtra("childId", childId))
        }
    }
}
