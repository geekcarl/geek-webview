package com.geekcarl.android.webview.base

import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * Created on 2020/6/23
 *
 * @author zengxiangxin
 */
open class GeekX5WebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        webView: WebView,
        s: String
    ): Boolean {
        webView.loadUrl(s)
        return true
    }

    override fun onReceivedSslError(
        webView: WebView,
        sslErrorHandler: SslErrorHandler,
        sslError: SslError
    ) {
        sslErrorHandler.proceed()
    }
}