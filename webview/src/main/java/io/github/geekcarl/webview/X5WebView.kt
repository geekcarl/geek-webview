package io.github.geekcarl.webview

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.github.geekcarl.webview.base.GeekX5WebChromeClient
import io.github.geekcarl.webview.base.GeekX5WebViewClient
import io.github.geekcarl.webview.base.IFileChooser
import com.tencent.smtt.export.external.interfaces.IX5WebSettings
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView

/**
 * Created on 2020/6/23
 *
 * @author geekCarl
 */
open class X5WebView : WebView, LifecycleObserver {
    private var mOnX5ScrollChangedListener: OnX5ScrollChangedListener? = null
    private lateinit var mGeekX5WebChromeClient: GeekX5WebChromeClient

    constructor(context: Context?) : super(context) {
        initWebViewSettings()
    }

    constructor(
        context: Context?,
        attributeSet: AttributeSet?
    ) : super(context, attributeSet) {
        initWebViewSettings()
    }

    constructor(
        context: Context?,
        attributeSet: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attributeSet, defStyleAttr) {
        initWebViewSettings()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSettings() {
        val webSetting = this.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = false
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        webSetting.loadWithOverviewMode = false
        webSetting.setAppCacheEnabled(true)
        webSetting.databaseEnabled = true
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        val extension = settingsExtension
        extension?.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.mixedContentMode = 0
        }
        webViewClient = GeekX5WebViewClient()
        mGeekX5WebChromeClient = GeekX5WebChromeClient(this, null)
        webChromeClient = mGeekX5WebChromeClient

        if (context is LifecycleOwner) {
            (context as LifecycleOwner).lifecycle.addObserver(this)
        }
    }

    fun setFileChooser(fileChooser: IFileChooser) {
        mGeekX5WebChromeClient.setFileChooser(fileChooser)
    }

    /**
     * 加载链接，参数会被自动encode
     *
     * @param url
     * @param args url 参数集合
     */
    fun loadUrl(url: String, args: Bundle?) {
        this.loadUrl(buildUrl(url, args))
    }

    /**
     * @param url
     * @param headers
     */
    override fun loadUrl(
        url: String,
        headers: Map<String, String>
    ) {
        super.loadUrl(url, headers)
    }

    private fun buildUrl(url: String, params: Bundle?): String {
        if (params == null) {
            return url
        }
        val builder = Uri.parse(url).buildUpon()
        for (key in params.keySet()) {
            val value = params[key].toString()
            if (!TextUtils.isEmpty(value)) {
                builder.appendQueryParameter(key, value)
            }
        }
        return builder.build().toString()
    }

    fun addJavascriptInterface(o: Any?) {
        super.addJavascriptInterface(o, "AgriMAP")
        // 添加Android兼容模式，适配老逻辑，后续跟前端确认统一 TODO
        super.addJavascriptInterface(o, "android")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        destroy()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mOnX5ScrollChangedListener != null) {
            mOnX5ScrollChangedListener!!.onScrollChanged(l, t, oldl, oldt, this)
        }
    }

    /**
     * webview 滑动监听
     *
     * @param mOnX5ScrollChangedListener
     */
    fun setOnX5ScrollChangedListener(mOnX5ScrollChangedListener: OnX5ScrollChangedListener?) {
        this.mOnX5ScrollChangedListener = mOnX5ScrollChangedListener
    }

    interface OnX5ScrollChangedListener {
        fun onScrollChanged(
            l: Int,
            t: Int,
            oldl: Int,
            oldt: Int,
            view: X5WebView?
        )
    }
}