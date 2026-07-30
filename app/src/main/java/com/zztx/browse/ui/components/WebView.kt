package com.zztx.browse.ui.components

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.zztx.browse.viewmodel.Tab

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(
    tab: Tab,
    onTitleChanged: (String) -> Unit,
    onFaviconChanged: (String?) -> Unit,
    onLoadFinished: () -> Unit
) {
    val context = LocalContext.current
    val webView = remember {
        android.webkit.WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36"
            
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onLoadFinished()
                    view?.title?.let { onTitleChanged(it) }
                }
            }
        }
    }

    AndroidView(
        factory = { webView },
        modifier = Modifier.fillMaxSize(),
        update = { view ->
            if (tab.url.isNotEmpty() && tab.url != "about:blank" && view.url != tab.url) {
                view.loadUrl(tab.url)
            }
        }
    )
}