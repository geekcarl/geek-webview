package com.geekcarl.android.webview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.RequiresApi
import com.geekcarl.android.bridge.*
import com.geekcarl.android.webview.proxy.WebViewClientProxy
import com.tencent.smtt.sdk.WebViewClient

open class GeekBridgeWebView : X5WebView, IWebView, IBridgeNative {
    private val TAG = "GeekBridgeWebView"
    lateinit var mBridgeHandler: IBridgeHandler
    lateinit var mBridgeNative: IBridgeNative
    private var mBridgeLoader: IBridgeLoader? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    private fun init() {
        BridgeManager(this).let {
            mBridgeHandler = it
            mBridgeNative = it
            mBridgeLoader = it
        }
        webViewClient = buildWebViewClientBridgeBind(webViewClient)
    }

    private fun buildWebViewClientBridgeBind(client: WebViewClient?): WebViewClientProxy {
        return client.let {
            if (it is WebViewClientProxy) it else WebViewClientProxy(it)
        }.apply {
            this.setBridgeLoader(mBridgeLoader)
        }
    }

    override fun setWebViewClient(client: WebViewClient?) {
        if (client is WebViewClientProxy) {
            super.setWebViewClient(client)
        } else {
            Log.d(TAG, "setWebViewClientBridgeBind")
            super.setWebViewClient(buildWebViewClientBridgeBind(client))
        }
    }

    override fun evaluateJavascript(
        script: String?,
        resultCallback: ValueCallback<String?>?
    ) {
        super.evaluateJavascript(
            script
        ) { value -> resultCallback?.let { it(value) } }
    }

    override fun post(runnable: () -> Unit): Boolean {
        return super.post { runnable() }
    }


    /**
     * call javascript registered handler
     * 调用H5声明方法
     *
     * @param handlerName handlerName
     * @param data        data
     * @param onBridgeCallBack    OnBridgeCallback
     */
    fun <T> callHandler(
        handlerName: String,
        data: Any?,
        clazz: Class<T>,
        onBridgeCallBack: OnBridgeCallBack<T>?
    ) {
        mBridgeHandler.call(handlerName, data, clazz, onBridgeCallBack)
    }

    /**
     * 同 [callHandler]
     * 内联函数kotlin使用
     */
    fun <T> callHandler(
        handlerName: String,
        data: Any?,
        clazz: Class<T>,
        onBridgeCallBack: SimpleBridgeCallBack<T>?
    ) {
        mBridgeHandler.call(handlerName, data, clazz, onBridgeCallBack)
    }

    /**
     * 同 [callHandler]
     * 内联函数kotlin使用
     */
    inline fun <reified T> callHandler(
        handlerName: String,
        data: Any?,
        onBridgeCallBack: OnBridgeCallBack<T>?
    ) {
        mBridgeHandler.call(handlerName, data, T::class.java, onBridgeCallBack)
    }

    /**
     * 同 [callHandler]
     * 内联函数kotlin使用
     */
    inline fun <reified T> callHandler(
        handlerName: String,
        data: Any?,
        onBridgeCallBack: SimpleBridgeCallBack<T>?
    ) {
        mBridgeHandler.call(handlerName, data, T::class.java, onBridgeCallBack)
    }

    /**
     * 同 [callHandler]
     * 内联函数kotlin使用
     */
    inline fun <reified T> callHandler(
        handlerName: String,
        onBridgeCallBack: OnBridgeCallBack<T>?
    ) {
        mBridgeHandler.call(handlerName, null, T::class.java, onBridgeCallBack)
    }

    /**
     * @param handler default handler,handle messages send by js without assigned handler name,
     * if js message has handler name, it will be handled by named handlers registered by native
     */
    fun setDefaultHandler(handler: BridgeHandler<Any>) {
        mBridgeHandler.registerDefault(handler)
    }

    /**
     * register handler,so that javascript can call it
     * 注册处理程序,以便javascript调用它
     *
     * @param handlerName handlerName
     * @param handler     BridgeHandler
     */
    fun <T> registerHandler(
        handlerName: String,
        clazz: Class<T>,
        handler: BridgeHandler<T>
    ) {
        mBridgeHandler.register(handlerName, clazz, handler)
    }

    /**
     * 同 [registerHandler]
     * 内联函数kotlin使用
     */
    inline fun <reified T> registerHandler(
        handlerName: String,
        handler: BridgeHandler<T>
    ) {
        mBridgeHandler.register(handlerName, T::class.java, handler)
    }

    /**
     * unregister handler
     *
     * @param handlerName
     */
    fun unRegisterHandler(handlerName: String) {
        mBridgeHandler.unRegister(handlerName)
    }

    /**
     * 直接发送数据
     */
    override fun sendData(data: Any) {
        mBridgeNative.sendData(data)
    }

    /**
     * 发送数据，并接收回执
     */
    override fun <T> sendData(
        data: Any,
        clazz: Class<T>,
        responseCallback: OnBridgeCallBack<T>?
    ) {
        mBridgeNative.sendData(data, clazz, responseCallback)
    }

    /**
     * (内联)发送数据，并接收回执
     */
    inline fun <reified T> sendData(
        data: Any,
        responseCallback: OnBridgeCallBack<T>?
    ) {
        mBridgeNative.sendData(data, T::class.java, responseCallback)
    }

    /**
     * Experimental. Use with caution
     *
     * @param function method name
     * @param values   method parameters
     */
    override fun callFunc(
        function: String,
        vararg values: Any?
    ) {
        mBridgeNative.callFunc(function, *values)
    }

    /**
     * Experimental. Use with caution
     *
     * @param function method name
     * @param callback value callback
     * @param values   method parameters
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun callFunc(
        function: String,
        callback: ValueCallback<String?>?,
        vararg values: Any?
    ) {
        mBridgeNative.callFunc(function, callback, *values)
    }
}