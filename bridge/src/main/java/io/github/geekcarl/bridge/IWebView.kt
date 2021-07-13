package io.github.geekcarl.bridge

import android.content.Context

/**
 * WebView能力接口
 */
interface IWebView {
    fun addJavascriptInterface(obj: Any?, interfaceName: String?)
    fun evaluateJavascript(
        script: String?,
        resultCallback: ValueCallback<String?>?
    )
    fun loadUrl(url: String?)
    fun post(runnable: () -> Unit): Boolean
    fun getContext(): Context
}