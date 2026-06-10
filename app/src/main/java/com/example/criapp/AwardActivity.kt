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
import com.example.criapp.adapters.AwardAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.AwardRepositoryImpl
import com.example.criapp.presentation.award.AwardViewModel
import com.example.criapp.presentation.award.AwardViewModelFactory
import com.example.criapp.presentation.common.UiState

class AwardActivity : BaseActivity() {

    private val viewModel: AwardViewModel by viewModels {
        AwardViewModelFactory(AwardRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var txtNoAwards: TextView
    private lateinit var tabDots: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_award)

        aLoadingDialog = ALoadingDialog(this)
        viewPager = findViewById(R.id.viewPager)
        txtNoAwards = findViewById(R.id.txtNoAwards)
        tabDots = findViewById(R.id.tabDots)
        viewPager.adapter = AwardAdapter(emptyList())
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
                        txtNoAwards.visibility = View.VISIBLE
                    } else {
                        val adapter = AwardAdapter(state.data)
                        viewPager.adapter = adapter
                        setupDots(state.data.size)
                        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) = updateDots(position)
                        })
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    DialogFactory.createErrorDialog(this, state.message) { finish() }.show()
                }
                else -> Unit
            }
        }

        viewModel.loadAwards(childId)
    }

    private fun setupDots(count: Int) {
        tabDots.removeAllViews()
        val dotSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics).toInt()
        val gapPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
        repeat(count) { i ->
            val dot = View(this).apply {
                background = ContextCompat.getDrawable(
                    this@AwardActivity,
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