//notation: js file can only use this kind of comments
//since comments will cause error when use in webview.loadurl,
//comments will be remove by java use regexp
(function() {
    if (window.WebViewJavascriptBridge) {
        return;
    }

    var receiveMessageQueue = [];
    var messageHandlers = {};

    var responseCallbacks = {};
    var uniqueId = 1;

    //set default messageHandler  初始化默认的消息线程
    function init(messageHandler) {
        if (WebViewJavascriptBridge._messageHandler) {
            console.log('WebViewJavascriptBridge.init called twice');
            return
        }
        WebViewJavascriptBridge._messageHandler = messageHandler;
        var receivedMessages = receiveMessageQueue;
        receiveMessageQueue = null;
        for (var i = 0; i < receivedMessages.length; i++) {
            _dispatchMessageFromNative(receivedMessages[i]);
        }
    }

    // 发送
    function send(data, responseCallback) {
        _doSend('send', data, responseCallback);
    }

    // 注册线程 往数组里面添加值
    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    // 调用线程
    function callHandler(handlerName, data, responseCallback) {
        _doSend(handlerName, data, responseCallback);
    }

    // sendMessage add message, 触发native处理 sendMessage
    function _doSend(handlerName, message, responseCallback) {
        var callbackId;
        if(typeof responseCallback === 'string'){
            callbackId = responseCallback;
        } else if (responseCallback) {
            callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            responseCallbacks[callbackId] = responseCallback;
        } else {
            callbackId = '';
        }

        var msgBody;
        if (typeof message === 'object') {
            msgBody = JSON.stringify(message);
        } else {
            msgBody = message;
        }

        var androidInterface = window.android;
        if (handlerName == 'send') {
            androidInterface.send(msgBody, callbackId);
        } else if (handlerName == 'response') {
            androidInterface.response(msgBody, callbackId);
        } else {
            androidInterface.send(handlerName, msgBody, callbackId);
        }
    }

    // 提供给native使用,
    function _dispatchMessageFromNative(messageJSON) {
        setTimeout(function() {
            var message = JSON.parse(messageJSON);
            var responseCallback;
            // java call finished, now need to call js callback function
            if (message.responseId) {
                responseCallback = responseCallbacks[message.responseId];
                if (!responseCallback) {
                    return;
                }
                responseCallback(message.responseData);
                delete responseCallbacks[message.responseId];
            } else {
                // 直接发送
                if (message.callbackId) {
                    var callbackResponseId = message.callbackId;
                    responseCallback = function(responseData) {
                        _doSend('response', responseData, callbackResponseId);
                    };
                }

                var handler = WebViewJavascriptBridge._messageHandler;
                // 查找指定handler
                if (message.handlerName) {
                    handler = messageHandlers[message.handlerName];
                }
                if (handler) {
                    try {
                        handler(message.data, responseCallback);
                    } catch (exception) {
                        if (typeof console != 'undefined') {
                            console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", JSON.stringify(message), exception);
                        }
                    }
                } else {
                    // 未注册方法，则返回未注册标识
                    _doSend('response', '__CallBack_Data_WebViewJavascriptBridge_Not_Find_Handler', callbackResponseId);
                }
            }
        });
    }

    // 提供给native调用,receiveMessageQueue 在会在页面加载完后赋值为null,所以
    function _handleMessageFromNative(messageJSON) {
        console.log('handle message: '+ messageJSON);
        if (receiveMessageQueue) {
            receiveMessageQueue.push(messageJSON);
        }
        _dispatchMessageFromNative(messageJSON);
    }

    var WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
        init: init,
        send: send,
        registerHandler: registerHandler,
        callHandler: callHandler,
        _handleMessageFromNative: _handleMessageFromNative
    };

    var doc = document;
    var readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('WebViewJavascriptBridgeReady');
    readyEvent.bridge = WebViewJavascriptBridge;
    doc.dispatchEvent(readyEvent);
})();
