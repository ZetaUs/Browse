package com.zztx.browse

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.addCallback

class MainActivity : ComponentActivity() {

    private var webView: WebView? = null
    private var inputEditText: EditText? = null
    private var urlEditText: EditText? = null
    private var inputContainer: LinearLayout? = null
    private var navBar: LinearLayout? = null
    private var backButton: ImageButton? = null
    private var forwardButton: ImageButton? = null
    private var refreshButton: ImageButton? = null
    private var homeButton: ImageButton? = null

    private val HOME_URL = "file:///android_asset/home.html"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 初始化控件
        webView = findViewById(R.id.webView)
        inputEditText = findViewById(R.id.inputEditText)
        urlEditText = findViewById(R.id.urlEditText)
        inputContainer = findViewById(R.id.inputContainer)
        navBar = findViewById(R.id.navBar)
        backButton = findViewById(R.id.backButton)
        forwardButton = findViewById(R.id.forwardButton)
        refreshButton = findViewById(R.id.refreshButton)
        homeButton = findViewById(R.id.homeButton)

        // 配置 WebView
        webView?.let { wv ->
            wv.settings.javaScriptEnabled = true
            wv.settings.domStorageEnabled = true
            wv.settings.loadWithOverviewMode = true
            wv.settings.useWideViewPort = true
            wv.settings.setSupportZoom(true)
            wv.settings.builtInZoomControls = true
            wv.settings.displayZoomControls = false
            wv.settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            wv.settings.mediaPlaybackRequiresUserGesture = false
            wv.settings.userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

            wv.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    // 更新地址栏
                    urlEditText?.setText(url)
                    updateNavigationButtons()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    // 更新地址栏
                    urlEditText?.setText(url)
                    updateNavigationButtons()
                    
                    // 检查是否回到首页
                    if (url == HOME_URL || url == "file:///android_asset/home.html") {
                        showHomeInput()
                    } else {
                        showNavBar()
                    }
                }
            }

            wv.webChromeClient = WebChromeClient()

            // 加载本地主页（随机风景背景）
            wv.loadUrl(HOME_URL)
        }

        // 中间输入框搜索功能
        inputEditText?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                inputEditText?.let { editText ->
                    val query = editText.text.toString().trim()
                    if (query.isNotEmpty()) {
                        webView?.let { wv ->
                            if (query.startsWith("http://") || query.startsWith("https://")) {
                                wv.loadUrl(query)
                            } else {
                                wv.loadUrl("https://cn.bing.com/?q=$query")
                            }
                        }
                        editText.text.clear()
                        // 隐藏输入法
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    }
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        // 顶部地址栏搜索功能
        urlEditText?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                urlEditText?.let { editText ->
                    val query = editText.text.toString().trim()
                    if (query.isNotEmpty()) {
                        webView?.let { wv ->
                            if (query.startsWith("http://") || query.startsWith("https://")) {
                                wv.loadUrl(query)
                            } else {
                                wv.loadUrl("https://cn.bing.com/?q=$query")
                            }
                        }
                        // 隐藏输入法
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    }
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        // 后退按钮
        backButton?.setOnClickListener {
            webView?.let { wv ->
                if (wv.canGoBack()) {
                    wv.goBack()
                }
            }
        }

        // 前进按钮
        forwardButton?.setOnClickListener {
            webView?.let { wv ->
                if (wv.canGoForward()) {
                    wv.goForward()
                }
            }
        }

        // 刷新按钮
        refreshButton?.setOnClickListener {
            webView?.reload()
        }

        // 主页按钮 - 返回随机风景背景
        homeButton?.setOnClickListener {
            webView?.loadUrl(HOME_URL)
        }

        // 处理返回键（新方式）
        onBackPressedDispatcher.addCallback(this) {
            webView?.let { wv ->
                if (wv.canGoBack()) {
                    wv.goBack()
                    return@addCallback
                }
            }
            // 如果没有历史记录，退出应用
            finish()
        }
    }

    // 更新导航按钮的显示状态
    private fun updateNavigationButtons() {
        webView?.let { wv ->
            // 根据是否可以后退/前进来显示/隐藏按钮
            backButton?.visibility = if (wv.canGoBack()) View.VISIBLE else View.GONE
            forwardButton?.visibility = if (wv.canGoForward()) View.VISIBLE else View.GONE
        }
    }

    // 显示首页输入框（居中）
    private fun showHomeInput() {
        inputContainer?.visibility = View.VISIBLE
        navBar?.visibility = View.GONE
    }

    // 显示导航栏（底部）
    private fun showNavBar() {
        inputContainer?.visibility = View.GONE
        navBar?.visibility = View.VISIBLE
    }

    // 销毁 WebView
    override fun onDestroy() {
        webView?.destroy()
        webView = null
        inputEditText = null
        urlEditText = null
        inputContainer = null
        navBar = null
        backButton = null
        forwardButton = null
        refreshButton = null
        homeButton = null
        super.onDestroy()
    }
}