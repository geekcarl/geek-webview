package io.github.geekcarl.bridge

/**
 * 桥Native接口
 */
interface IBridgeNative {
    /**
     * 原生发送数据
     */
    fun sendData(data: Any)

    /**
     * 原生发送数据，并响应回调
     */
    fun <T> sendData(
        data: Any,
        clazz: Class<T>,
        responseCallback: OnBridgeCallBack<T>?
    )

    /**
     * 原生调用js方法，可传递可选参数
     * e.g callFunc("test", 1,2,3)
     */
    fun callFunc(function: String, vararg values: Any?)

    /**
     * 原生调用js方法，并响应回执结果，可传递参数
     * e.g callFunc("test",{back-> }, 1,2,3)
     */
    fun callFunc(
        function: String,
        callback: ValueCallback<String?>?,
        vararg values: Any?
    )
}

/**
 * 桥web通信接口
 */
internal interface IBridgeWeb {
    /**
     * 从web接收消息
     */
    fun receiveMessageFromWeb(handlerName: String?, data: String?, callbackId: String?)

    /**
     * 从web接收回复消息
     */
    fun receiveResponseFromWeb(data: String?, responseId: String?)
}

/**
 * 桥连接器，发送+回复
 */
internal interface IBridgeConnect : IBridgeNative, IBridgeWeb

interface IBridgeHandler {
    /**
     * 原生调用js方法
     */
    fun <T> call(
        handlerName: String,
        data: Any?,
        clazz: Class<T>,
        onBridgeCallBack: OnBridgeCallBack<T>?
    )

    /**
     * 原生注册方法，供js调用
     */
    fun <T> register(
        handlerName: String,
        clazz: Class<T>,
        handler: BridgeHandler<T>
    )

    /**
     * 原生注册默认方法，供js调用未找到方法时使用
     */
    fun registerDefault(handler: BridgeHandler<Any>)

    /**
     * 原生反注册方法
     */
    fun unRegister(handlerName: String)
}

/**
 * 桥加载
 */
interface IBridgeLoader {
    /**
     * bridge加载
     */
    fun onBridgeLoad()
}