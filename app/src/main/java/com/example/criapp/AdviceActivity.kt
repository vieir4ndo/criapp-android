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
import com.example.criapp.adapters.AdviceAdapter
import com.example.criapp.data.local.PreferencesManager
import com.example.criapp.data.remote.RetrofitProvider
import com.example.criapp.data.repository.AdviceRepositoryImpl
import com.example.criapp.presentation.common.UiState
import com.example.criapp.presentation.advice.AdviceViewModel
import com.example.criapp.presentation.advice.AdviceViewModelFactory

class AdviceActivity : AppCompatActivity() {

    private val viewModel: AdviceViewModel by viewModels {
        AdviceViewModelFactory(AdviceRepositoryImpl(RetrofitProvider.create(PreferencesManager(this))))
    }
    private var aLoadingDialog: ALoadingDialog? = null
    private lateinit var viewPager: ViewPager2
    private lateinit var txtNoAdvices: TextView
    private lateinit var tabDots: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advice)

        aLoadingDialog = ALoadingDialog(this)
        viewPager = findViewById(R.id.viewPager)
        txtNoAdvices = findViewById(R.id.txtNoAdvices)
        tabDots = findViewById(R.id.tabDots)
        viewPager.adapter = AdviceAdapter(emptyList())
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        viewModel.state.observe(this) { state ->
            when (state) {
                is UiState.Loading -> aLoadingDialog?.show()
                is UiState.Success -> {
                    aLoadingDialog?.cancel()
                    if (state.data.isEmpty()) {
                        viewPager.visibility = View.GONE
                        tabDots.visibility = View.GONE
                        txtNoAdvices.visibility = View.VISIBLE
                    } else {
                        val adapter = AdviceAdapter(state.data)
                        viewPager.adapter = adapter
                        setupDots(state.data.size)
                        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) = updateDots(position)
                        })
                    }
                }
                is UiState.Error -> {
                    aLoadingDialog?.cancel()
                    Toast.makeText(this, getString(R.string.error_load_advices), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        viewModel.loadAdvices()
    }

    private fun setupDots(count: Int) {
        tabDots.removeAllViews()
        val dotSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7f, resources.displayMetrics).toInt()
        val gapPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, resources.displayMetrics).toInt()
        repeat(count) { i ->
            val dot = View(this).apply {
                background = ContextCompat.getDrawable(
                    this@AdviceActivity,
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
