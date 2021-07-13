package io.github.geekcarl.bridge

/**
 * Created on 2021/4/9
 * <p>
 *
 * @author geekCarl
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