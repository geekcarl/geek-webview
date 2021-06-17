
## webview容器组件
---

 ### **使用**
* 1，添加依赖
* 2，设置json转化代理（如果bean类使用kotlin编写，必须使用google gson做转化），
  > JsonHandlerProxy.setProxy(proxy)
* 3，使用GeekBridgeWebView 加载页面

 ### **原生与H5交互(桥接方式)**
* 1，原生调H5方法并传数据：
  > webview.callHandler(handlerName, data, clazz, callback)
* 2，原生只调H5方法：
  > webview.callHandler(handlerName, clazz, callback)
* 3，原生注册方法供H5调用：
  > webview.registerHandler(handlerName, clazz, handler)
* 4，原生注册默认方法(当找不到指定handlerName处理器时候使用，可不指定)：
  > webview.setDefaultHandler(handler) 

* 5，原生直接发送数据：
  > webview.sendData(data)
* 6，原生直接发送数据并接收回执：
  > webview.sendData(data, callback)

 ### **原生与H5交互(直接方式)** 
* 1，原生调用js顶层方法： 

  > webview.callFunc(funName, arg1,arg2...) 
* 2，原生调用js顶层方法并接收回执（target sdk> 19）： 
  > webview.callFunc(funName, callback, arg1,arg2...) 