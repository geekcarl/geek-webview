package com.geekcarl.android.bridge

/**
 * Created on 2021/4/16
 * <p>
 *
 * @author zengxiangxin
 */

internal interface ICallBack {
    /**
     * native回调，供js上报数据回调调用
     */
    fun callBack(data: String?)
}

internal class CallBackWrapper<T>(
    private val callback: OnBridgeCallBack<T>?,
    private val clazz: Class<T>
) : ICallBack {

    override fun callBack(data: String?) {
        BridgeUtil.log("CallBackWrapper callBack: $data")
        callback?.let { callback ->
            when {
                data == BridgeUtil.JS_NOT_FIND_HANDLER -> {
                    // 如果未找到handler，返回未找到handler错误类型
                    callback.onError(ErrorCode.UNREGISTERED)
                }
                data.isNullOrEmpty() -> {
                    // 数据为空，直接返回成功，null值
                    callback.onSuccess(null)
                }
                else -> {
                    try {
                        // 数据转化为指定类型
                        callback.onSuccess(BridgeUtil.fromJsonByType(data, clazz))
                    } catch (throwable: Throwable) {
                        // 数据转化失败，直接返回失败
                        BridgeUtil.log(throwable.message, isError = true)
                        callback.onError(ErrorCode.DATA_ERROR)
                    }
                }
            }
        }
    }
}