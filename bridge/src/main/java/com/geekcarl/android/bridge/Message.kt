package com.geekcarl.android.bridge

/**
 * Created on 2021/4/9
 * <p>
 *
 * @author zengxiangxin
 *
 * 消息请求
 */
internal class RequestMessage {
    var callbackId: String? = null
    var data: String? = null
    var handlerName: String? = null
}

/**
 * 消息回复
 */
internal class ResponseMessage {
    var responseId: String? = null
    var responseData: String? = null
}