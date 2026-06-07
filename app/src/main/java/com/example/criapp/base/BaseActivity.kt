package com.example.criapp.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.criapp.R

open class BaseActivity : AppCompatActivity() {

    open val statusBarColorRes: Int get() = R.color.purple
    open val navigationBarColorRes: Int get() = R.color.dark_purple
    open val lightStatusBars: Boolean get() = false
    open val lightNavigationBars: Boolean get() = false

    private val bannerHandler = Handler(Looper.getMainLooper())
    private var activeBanner: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySystemBarsColors()
    }

    fun showSuccessSnackbar(message: String) {
        showBanner(message, R.color.green, R.drawable.ic_check)
    }

    fun showErrorSnackbar(message: String) {
        showBanner(message, R.color.red, R.drawable.ic_error_circle)
    }

    private fun showBanner(message: String, colorRes: Int, iconRes: Int) {
        val decor = window.decorView as ViewGroup

        activeBanner?.let {
            bannerHandler.removeCallbacksAndMessages(null)
            decor.removeView(it)
        }

        val banner = LayoutInflater.from(this)
            .inflate(R.layout.banner_notification, decor, false)
        banner.findViewById<TextView>(R.id.banner_message).text = message
        banner.findViewById<ImageView>(R.id.banner_icon).setImageResource(iconRes)
        banner.backgroundTintList =
            android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, colorRes))

        val statusBarHeight = run {
            val id = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (id > 0) resources.getDimensionPixelSize(id) else 0
        }
        val margin = resources.getDimensionPixelSize(R.dimen.margin_top)

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = statusBarHeight + margin
            leftMargin = margin
            rightMargin = margin
        }

        banner.layoutParams = params
        banner.translationY = -300f
        banner.alpha = 0f
        decor.addView(banner)
        activeBanner = banner

        banner.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(250)
            .start()

        val dismissBanner = {
            bannerHandler.removeCallbacksAndMessages(null)
            banner.animate()
                .translationY(-300f)
                .alpha(0f)
                .setDuration(200)
                .withEndAction { decor.removeView(banner) }
                .start()
            activeBanner = null
        }

        banner.findViewById<ImageView>(R.id.banner_close).setOnClickListener { dismissBanner() }

        bannerHandler.postDelayed({
            banner.animate()
                .translationY(-300f)
                .alpha(0f)
                .setDuration(200)
                .withEndAction { decor.removeView(banner) }
                .start()
            activeBanner = null
        }, 2500)
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
