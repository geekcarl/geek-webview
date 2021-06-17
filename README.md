# geek-webview

webview容器组件

1，添加依赖
2，设置json转化代理（如果bean类使用kotlin，必须使用google gson做转化），JsonHandlerProxy.setProxy(proxy)
3，使用GeekBridgeWebView 加载页面

4，-----以下使用桥通信：
原生调H5方法并传数据：webview.callHandler(handlerName, data, clazz, callback)
原生只调H5方法：webview.callHandler(handlerName, clazz, callback)

原生注册方法供H5调用：webview.registerHandler(handlerName, clazz, handler)
原生注册默认方法(当找不到指定handlerName处理器时候使用，可不指定)：webview.setDefaultHandler(handler)

原生直接发送数据：webview.sendData(data)
原生直接发送数据并接收回执：webview.sendData(data, callback)

-----以下使用直接调用js顶层方法：
原生调用js顶层方法：webview.callFunc(funName, arg1,arg2...)
原生调用js顶层方法并接收回执（target sdk> 19）：webview.callFunc(funName, callback, arg1,arg2...)