package com.example.criapp

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.criapp.services.DialogFactory
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.criapp.base.BaseActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.criapp.adapters.RecommendationAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.RecommendationRepositoryImpl
import com.example.criapp.models.responses.RecommendationResponse
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.recommendation.RecommendationViewModel
import com.example.criapp.presentation.recommendation.RecommendationViewModelFactory

class RecommendationActivity : BaseActivity() {

    private val viewModel: RecommendationViewModel by viewModels {
        RecommendationViewModelFactory(RecommendationRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var txtNoRecommendations: TextView
    private lateinit var tabDots: LinearLayout

    private var currentList: MutableList<RecommendationResponse> = mutableListOf()
    private lateinit var childId: String
    private var pendingPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)

        aLoadingDialog = ALoadingDialog(this)
        viewPager = findViewById(R.id.viewPager)
        txtNoRecommendations = findViewById(R.id.txtNoRecommendations)
        tabDots = findViewById(R.id.tabDots)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        childId = intent.getStringExtra("childId") ?: return

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    if (state.data.isEmpty()) {
                        viewPager.visibility = View.GONE
                        tabDots.visibility = View.GONE
                        txtNoRecommendations.visibility = View.VISIBLE
                    } else {
                        currentList = state.data.toMutableList()
                        renderAdapter(currentList)
                        val targetPage = pendingPage.coerceAtMost(currentList.size - 1)
                        viewPager.setCurrentItem(targetPage, false)
                        pendingPage = 0
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, getString(R.string.error_load_recommendations)) { finish() }.show()
                }
                else -> Unit
            }
        }

        viewModel.checkpointState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    setAdapterButtonsEnabled(false)
                }
                is UiState.Success -> {
                    setAdapterButtonsEnabled(true)
                    Toast.makeText(this, getString(R.string.checkpoint_saved), Toast.LENGTH_SHORT).show()
                    pendingPage = viewPager.currentItem
                    viewModel.loadRecommendations(childId)
                }
                is UiState.Error -> {
                    setAdapterButtonsEnabled(true)
                    Toast.makeText(this, getString(R.string.error_save_checkpoint), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.loadRecommendations(childId)
    }

    private fun renderAdapter(list: List<RecommendationResponse>) {
        val adapter = RecommendationAdapter(list) { recommendationId, completed ->
            viewModel.registerCheckpoint(childId, recommendationId, completed)
        }
        viewPager.adapter = adapter
        setupDots(list.size)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateDots(position)
        })
    }

    private fun setAdapterButtonsEnabled(enabled: Boolean) {
        (viewPager.adapter as? RecommendationAdapter)?.setButtonsEnabled(enabled)
    }

    private fun setupDots(count: Int) {
        tabDots.removeAllViews()
        val dotSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics).toInt()
        val gapPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
        repeat(count) { i ->
            val dot = View(this).apply {
                background = ContextCompat.getDrawable(
                    this@RecommendationActivity,
                    if (i == 0) R.drawable.dot_active else R.drawable.dot_inactive
                )
            }
            val params = LinearLayout.LayoutParams(dotSizePx, dotSizePx).apply {
                if (i > 0) marginStart = gapPx
            }
            tabDots.addView(dot, params)
        }
    }

    private fun updateDots(selected: Int) {
        for (i in 0 until tabDots.childCount) {
            tabDots.getChildAt(i).background = ContextCompat.getDrawable(
                this,
                if (i == selected) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }
}
