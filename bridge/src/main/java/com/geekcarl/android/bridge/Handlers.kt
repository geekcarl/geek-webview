package com.geekcarl.android.bridge

/**
 * Created on 2021/4/14
 * <p>
 * @author zengxiangxin
 * 消息处理器
 */
internal interface IHandler {
    fun hand(data: String?, callback: OnBridgeFeedBack<Any>?)
}

internal class HandlerWrapper<T>(
    private val handler: BridgeHandler<T>,
    private val clazz: Class<T>
) : IHandler {

    override fun hand(data: String?, callback: OnBridgeFeedBack<Any>?) {
        data?.let {
            try {
                BridgeUtil.fromJsonByType(it, clazz)
            } catch (throwable: Throwable) {
                BridgeUtil.log(throwable.message, isError = true)
                null
            }
        }?.let { t ->
            handler.hand(t) { resp ->
                callback?.let { it(resp) }
            }
        }
    }
}
