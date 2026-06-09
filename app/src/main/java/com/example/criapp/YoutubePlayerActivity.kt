package com.example.criapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.example.criapp.base.BaseActivity

class YoutubePlayerActivity : BaseActivity() {

    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
    }

    private lateinit var webView: WebView
    private lateinit var fullscreenContainer: FrameLayout
    private var customView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        webView = findViewById(R.id.webView)
        fullscreenContainer = findViewById(R.id.fullscreen_container)

        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL) ?: run {
            finish()
            return
        }

        val videoId = Uri.parse(videoUrl).getQueryParameter("v") ?: run {
            finish()
            return
        }

        setupWebView()
        webView.loadUrl("https://www.youtube.com/embed/$videoId?autoplay=1&rel=0")
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return false
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                customView = view
                fullscreenContainer.addView(view)
                fullscreenContainer.visibility = View.VISIBLE
                webView.visibility = View.GONE
            }

            override fun onHideCustomView() {
                customView?.let { fullscreenContainer.removeView(it) }
                customView = null
                fullscreenContainer.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        if (customView != null) {
            webView.webChromeClient?.onHideCustomView()
        } else if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
