package com.example.criapp

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
    private lateinit var childBookId: String
    private var pendingPage = 0
    private var silentReload = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendation)

        aLoadingDialog = ALoadingDialog(this)
        viewPager = findViewById(R.id.viewPager)
        txtNoRecommendations = findViewById(R.id.txtNoRecommendations)
        tabDots = findViewById(R.id.tabDots)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        childBookId = intent.getStringExtra("childBookId") ?: return

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> if (!silentReload) aLoadingDialog?.show()
                is UiState.Success -> {
                    silentReload = false
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
                    silentReload = false
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message) { finish() }.show()
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
                    val assimilated = state.data
                    if (assimilated) {
                        DialogFactory.createSuccessDialog(this, getString(R.string.recommendation_assimilated)) {
                            pendingPage = (viewPager.currentItem - 1).coerceAtLeast(0)
                            silentReload = true
                            viewModel.loadRecommendationsByChildBookId(childBookId)
                        }.show()
                    } else {
                        showSuccessSnackbar(getString(R.string.checkpoint_saved))
                        pendingPage = viewPager.currentItem
                        silentReload = true
                        viewModel.loadRecommendationsByChildBookId(childBookId)
                    }
                }
                is UiState.Error -> {
                    setAdapterButtonsEnabled(true)
                    showErrorSnackbar(state.message)
                }
                else -> Unit
            }
        }

        viewModel.loadRecommendationsByChildBookId(childBookId)
    }

    private fun renderAdapter(list: List<RecommendationResponse>) {
        val adapter = RecommendationAdapter(list, { childBookRecommendationId, completed ->
            viewModel.registerCheckpoint(childBookId, childBookRecommendationId, completed)
        }, {
            showInfoSnackbar(getString(R.string.checkpoint_already_answered))
        })
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
