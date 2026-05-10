package com.example.criapp

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.criapp.adapters.RecommendationAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.RecommendationRepositoryImpl
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.recommendation.RecommendationViewModel
import com.example.criapp.presentation.recommendation.RecommendationViewModelFactory

class RecommendationActivity : AppCompatActivity() {

    private val viewModel: RecommendationViewModel by viewModels {
        RecommendationViewModelFactory(RecommendationRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var txtNoRecommendations: TextView
    private lateinit var tabDots: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)

        aLoadingDialog = ALoadingDialog(this)
        viewPager = findViewById(R.id.viewPager)
        txtNoRecommendations = findViewById(R.id.txtNoRecommendations)
        tabDots = findViewById(R.id.tabDots)
        viewPager.adapter = RecommendationAdapter(emptyList())
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val childId = intent.getStringExtra("childId") ?: return
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
                        val adapter = RecommendationAdapter(state.data)
                        viewPager.adapter = adapter
                        setupDots(state.data.size)
                        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) = updateDots(position)
                        })
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.error_load_recommendations), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.loadRecommendations(childId)
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
