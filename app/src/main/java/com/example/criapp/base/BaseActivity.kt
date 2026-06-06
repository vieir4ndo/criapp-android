package com.example.criapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.criapp.R

open class BaseActivity : AppCompatActivity() {

    open val statusBarColorRes: Int get() = R.color.purple
    open val navigationBarColorRes: Int get() = R.color.dark_purple
    open val lightStatusBars: Boolean get() = false
    open val lightNavigationBars: Boolean get() = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySystemBarsColors()
    }

    private fun applySystemBarsColors() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = getColor(statusBarColorRes)
        window.navigationBarColor = getColor(navigationBarColorRes)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightStatusBars
            isAppearanceLightNavigationBars = lightNavigationBars
        }
    }
}
