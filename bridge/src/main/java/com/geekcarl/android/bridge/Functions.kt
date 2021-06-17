package com.geekcarl.android.bridge

/**
 * Created on 2021/4/7
 * <p>
 * 回调
 * @author zengxiangxin
 */
interface OnBridgeCallBack<T> {
    fun onSuccess(data: T?)
    fun onError(errorCode: ErrorCode)
}

/**
 * 不关注error情况的简单回调
 */
abstract class SimpleBridgeCallBack<T> : OnBridgeCallBack<T> {
    override fun onError(errorCode: ErrorCode) {
        BridgeUtil.log("OnBridgeCallBack error: $errorCode", isError = true)
    }
}

/**
 * 桥接反馈结果
 */
typealias OnBridgeFeedBack<T> = (data: T?) -> Unit

/**
 * 消息处理
 */
interface BridgeHandler<T> {
    fun hand(data: T?, onFeedBack: OnBridgeFeedBack<Any>?)
}
/**
 * 值回调
 */
typealias ValueCallback<T> = (data: T?) -> Unit