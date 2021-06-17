package com.geekcarl.android.bridge

/**
 * Created on 2021/4/14
 * <p>
 *
 * @author zengxiangxin
 */
interface JsonHandler {
    /**
     * 对象转json
     */
    fun toJson(obj: Any?): String?

    /**
     * json转对象
     */
    fun <T> fromJson(json: String, clazz: Class<T>): T?
}

object JsonHandlerProxy {
    var defaultHandler: JsonHandler? = null

    fun setProxy(handler: JsonHandler) {
        defaultHandler = handler
    }

    private fun throwStateException() {
        throw IllegalStateException("please call setJsonHandler first to init default json handler")
    }

    fun toJson(obj: Any?): String? {
        defaultHandler ?: throwStateException()
        return defaultHandler?.toJson(obj)
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        defaultHandler ?: throwStateException()
        return defaultHandler?.fromJson(json, clazz)
    }

}