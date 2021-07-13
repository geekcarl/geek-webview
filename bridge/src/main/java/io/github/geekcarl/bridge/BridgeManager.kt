package io.github.geekcarl.bridge

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.collection.ArrayMap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.util.*
import kotlin.collections.HashMap

/**
 * 桥接实现类
 * Created on 2021/4/2
 * @author geekCarl
 */
class BridgeManager(
    private val mWebView: IWebView
) : IBridgeConnect,
    IBridgeHandler,
    IBridgeLoader,
    LifecycleObserver {

    private val mMessageHandlers: HashMap<String, IHandler> = HashMap()
    private val mCallbacks: MutableMap<String, ICallBack> = ArrayMap()
    private var mMessages: MutableList<Any>? = ArrayList()

    private var defaultHandler: IHandler? = null

    @Volatile
    private var mUniqueId: Long = 0
    var isJSLoaded = false
        private set

    override fun onBridgeLoad() {
        isJSLoaded = false
        BridgeUtil.webViewLoadLocalJs(mWebView)
        isJSLoaded = true
        mMessages = mMessages?.let {
            for (message in it) {
                dispatchMessage(message)
            }
            null
        }
    }

    override fun <T> call(
        handlerName: String,
        data: Any?,
        clazz: Class<T>,
        onBridgeCallBack: OnBridgeCallBack<T>?
    ) {
        doSend(handlerName, data, clazz, onBridgeCallBack)
    }

    override fun <T> register(
        handlerName: String,
        clazz: Class<T>,
        handler: BridgeHandler<T>
    ) {
        mMessageHandlers[handlerName] = HandlerWrapper(handler, clazz)
    }

    override fun registerDefault(handler: BridgeHandler<Any>) {
        defaultHandler = HandlerWrapper(handler, Any::class.java)
    }

    override fun unRegister(handlerName: String) {
        mMessageHandlers.remove(handlerName)
    }

    override fun sendData(data: Any) {
        sendData(data, Any::class.java, null)
    }

    override fun <T> sendData(
        data: Any,
        clazz: Class<T>,
        responseCallback: OnBridgeCallBack<T>?
    ) {
        doSend(null, data, clazz, responseCallback)
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
        postRun {
            val jsCommand = BridgeUtil.generateJavascriptFunctionStr(
                function,
                *values
            )
            BridgeUtil.log("callFunc -> $jsCommand")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript(jsCommand, null)
            } else {
                mWebView.loadUrl(jsCommand)
            }
        }
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
        postRun {
            val jsCommand = BridgeUtil.generateJavascriptFunctionStr(
                function,
                *values
            )
            BridgeUtil.log("callFunc -> $jsCommand")
            mWebView.evaluateJavascript(jsCommand, callback)
        }
    }

    /**
     * 保存message到消息队列
     *
     * @param handlerName      handlerName
     * @param data             data
     * @param responseCallback OnBridgeCallback
     */
    @Synchronized
    private fun <T> doSend(
        handlerName: String?,
        data: Any?,
        clazz: Class<T>,
        responseCallback: OnBridgeCallBack<T>?
    ) {
        val request = RequestMessage().apply {
            this.handlerName = handlerName
            this.data = BridgeUtil.toJsonByType(data)
            this.callbackId = responseCallback?.let {
                val callbackId =
                    BridgeUtil.generateCallbackId(++mUniqueId)
                mCallbacks[callbackId] = CallBackWrapper(it, clazz)
                callbackId
            }
        }
        queueMessage(request)
    }

    /**
     * 添加到消息集合或者分发消息
     *
     * @param message Message
     */
    private fun queueMessage(message: Any) {
        mMessages?.add(message)
            ?: dispatchMessage(message)
    }

    /**
     * 分发message
     *
     * @param message 消息实体
     */
    private fun dispatchMessage(message: Any) {
        BridgeUtil.toJsonByType(message)?.let {
            val javascriptCommand = BridgeUtil.messageJsonToJsCommand(it)
            postRun {
                BridgeUtil.log("dispatchMessage -> $javascriptCommand")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(javascriptCommand, null)
                } else {
                    mWebView.loadUrl(javascriptCommand)
                }
            }
        }
    }

    override fun receiveMessageFromWeb(handlerName: String?, data: String?, callbackId: String?) {
        val handler = handlerName?.let {
            mMessageHandlers[it]
        } ?: defaultHandler
        handler?.let {
            postRun {
                it.hand(data) { respData -> sendResponse(respData, callbackId) }
            }
        } ?: sendResponse(BridgeUtil.JS_NOT_FIND_HANDLER, callbackId)
    }

    override fun receiveResponseFromWeb(data: String?, responseId: String?) {
        responseId?.let { id ->
            mCallbacks[id]?.let { func ->
                postRun {
                    func.callBack(data)
                    mCallbacks.remove(id)
                }
            }
        }
    }

    private fun sendResponse(data: Any?, callbackId: String?) {
        callbackId?.let {
            ResponseMessage().apply {
                responseId = it
                responseData = BridgeUtil.toJsonByType(data)
            }
        }?.let(this::dispatchMessage)
    }

    private fun postRun(runnable: () -> Unit): Boolean = mWebView.post(runnable)

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mCallbacks.clear()
        mMessageHandlers.clear()
    }

    init {
        mWebView.addJavascriptInterface(
            BridgeInterface(this),
            BridgeUtil.ANDROID_INTERFACE
        )
        if (mWebView.getContext() is LifecycleOwner) {
            (mWebView.getContext() as LifecycleOwner).lifecycle.addObserver(this)
        }
    }
}