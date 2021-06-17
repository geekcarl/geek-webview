package com.geekcarl.android.bridge

import android.content.Context
import android.os.SystemClock
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URLEncoder

internal object BridgeUtil {
    private const val TAG = "GeekBridge"
    private const val JAVA_SCRIPT = "WebViewJavascriptBridge.js"
    private const val UNDERLINE_STR = "_"
    private const val CALLBACK_ID_FORMAT = "JAVA_CB_%s"
    private const val JS_HANDLE_MESSAGE_FROM_JAVA =
        "javascript:WebViewJavascriptBridge._handleMessageFromNative('%s');"
    private const val JAVASCRIPT_STR = "javascript:%s;"

    const val JS_NOT_FIND_HANDLER = "__CallBack_Data_WebViewJavascriptBridge_Not_Find_Handler"
    const val ANDROID_INTERFACE = "android"
    private val JAVASCRIPT_FORMAT_REGEX = Regex("^\\s*//.*")

    /**
     * js 文件将注入为第一个script引用
     * @param view WebView
     * @param url url
     */
    fun webViewLoadJs(view: IWebView, url: String) {
        var js = "var newscript = document.createElement(\"script\");"
        js += "newscript.src=\"$url\";"
        js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);"
        view.loadUrl("javascript:$js")
    }

    /**
     * 这里只是加载lib包中assets中的 WebViewJavascriptBridge.js
     * @param view webview
     * @param path 路径
     */
    fun webViewLoadLocalJs(view: IWebView) {
        assetFile2Str(view.getContext(), JAVA_SCRIPT)?.let {
            view.loadUrl("javascript:$it")
        } ?: log("webViewLoadLocalJs error", isError = true)
    }

    /**
     * 将消息数据封装成可执行的javascript脚本
     * @param message 消息内容
     */
    fun messageJsonToJsCommand(message: String): String {
        var messageJson = message
        //escape special characters for json string  为json字符串转义特殊字符
        messageJson = messageJson.replace("(\\\\)([^utrn])".toRegex(), "\\\\\\\\$1$2")
        messageJson = messageJson.replace("(?<=[^\\\\])(\")".toRegex(), "\\\\\"")
        messageJson = messageJson.replace("(?<=[^\\\\])(\')".toRegex(), "\\\\\'")
        messageJson = messageJson.replace("%7B".toRegex(), URLEncoder.encode("%7B"))
        messageJson = messageJson.replace("%7D".toRegex(), URLEncoder.encode("%7D"))
        messageJson = messageJson.replace("%22".toRegex(), URLEncoder.encode("%22"))
        return JS_HANDLE_MESSAGE_FROM_JAVA.format(messageJson)
    }

    /**
     *  生成callback唯一Id
     *  @param uniqueId 唯一id
     */
    fun generateCallbackId(uniqueId: Long): String {
        return CALLBACK_ID_FORMAT.format(
            uniqueId.toString() + (UNDERLINE_STR + SystemClock.currentThreadTimeMillis())
        )
    }

    /**
     * 根据方法名和参数，生成可以执行的javaScript执行脚本
     * @param funcName 方法名
     * @param args 方法可变参数
     */
    fun generateJavascriptFunctionStr(
        funcName: String,
        vararg args: Any?
    ): String {
        val argsString = args.joinToString(",") {
            "'${toJsonByType(it)}'"
        }
        val functionStr = "%s(%s)".format(funcName, argsString)
        return JAVASCRIPT_STR.format(functionStr)
    }

    /**
     * 将string类型转化为指定类型
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> fromJsonByType(data: String?, clazz: Class<T>): T? {
        log("fromJsonByType : $clazz")
        data ?: return null
        return if (clazz == String::class.java
            || clazz == Character::class.java
            || clazz == Any::class.java
        ) {
            data as T
        } else if (clazz == Int::class.java) {
            data.toInt() as T
        } else if (clazz == Boolean::class.java) {
            data.toBoolean() as T
        } else if (clazz == Float::class.java) {
            data.toFloat() as T
        } else if (clazz == Long::class.java) {
            data.toLong() as T
        } else if (clazz == Double::class.java) {
            data.toDouble() as T
        } else if (clazz == Short::class.java) {
            data.toShort() as T
        } else if (clazz == Byte::class.java) {
            data.toByte() as T
        } else if (clazz == Void::class.java) {
            null
        } else if (!clazz.isPrimitive) {
            JsonHandlerProxy.fromJson(data, clazz)
        } else {
            data as T
        }
    }

    /**
     * 将数据类型转化为json
     * 如果数据是基础类型，则直接toString
     * 否则使用json转化
     * @param data 待转化的数据对象
     */
    fun toJsonByType(data: Any?): String? {
        data ?: return null
        if (data.javaClass.isPrimitive
            || data is CharSequence
            || data is Number
            || data is Boolean
        ) {
            return data.toString()
        }
        return JsonHandlerProxy.toJson(data)
    }

    /**
     * 打印错误log
     */
    fun log(log: String?, isError: Boolean = false) {
        if (isError) {
            Log.e(TAG, log)
        } else {
            Log.d(TAG, log)
        }
    }

    /**
     * 解析assets文件夹里面的代码,去除注释,取可执行的代码
     * @param c context
     * @param path 路径
     * @return 可执行代码
     */
    private fun assetFile2Str(c: Context, path: String): String? {
        var inputStream: InputStream? = null
        try {
            inputStream = c.assets.open(path)
            val bufferedReader =
                BufferedReader(InputStreamReader(inputStream))
            var line: String?
            val sb = StringBuilder()
            do {
                line = bufferedReader.readLine()
                if (line != null
                    && !line.matches(JAVASCRIPT_FORMAT_REGEX)
                ) { // 去除注释
                    sb.append(line)
                }
            } while (line != null)
            bufferedReader.close()
            inputStream.close()
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}