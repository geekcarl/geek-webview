package io.github.geekcarl.bridge

import android.webkit.JavascriptInterface

/**
 * 桥接收器
 * 通过 [JavascriptInterface] 注解方法接收
 *
 * Created on 2021/4/2
 * @author geekCarl
 */
internal class BridgeInterface(private val mBridgeWeb: io.github.geekcarl.bridge.IBridgeWeb) {

    @JavascriptInterface
    fun send(data: String?, callbackId: String?) {
        BridgeUtil.log(
            "BridgeInterface send data:$data, " +
                    "callbackId:$callbackId, " +
                    "threadName:${Thread.currentThread().name}"
        )
        mBridgeWeb.receiveMessageFromWeb(null, data, callbackId)
    }

    @JavascriptInterface
    fun send(
        handlerName: String?,
        data: String?,
        callbackId: String?
    ) {
        BridgeUtil.log(
            "BridgeInterface send handlerName:[$handlerName], " +
                    "data:$data, " +
                    "callbackId:$callbackId, " +
                    "threadName:${Thread.currentThread().name}"
        )
        mBridgeWeb.receiveMessageFromWeb(handlerName, data, callbackId)
    }

    @JavascriptInterface
    fun response(data: String?, responseId: String?) {
        BridgeUtil.log(
            "BridgeInterface response data:$data, " +
                    "responseId:$responseId, " +
                    "threadName:${Thread.currentThread().name}"
        )
        mBridgeWeb.receiveResponseFromWeb(data, responseId)
    }
}