//iOS 交互声明
function connectWebViewJavascriptBridgeIOS (callback) {
  if (window.WebViewJavascriptBridge) {
    return callback(window.WebViewJavascriptBridge)
  }
  if (window.WVJBCallbacks) {
    return window.WVJBCallbacks.push(callback)
  }
  window.WVJBCallbacks = [callback]
  let WVJBIframe = document.createElement('iframe')
  WVJBIframe.style.display = 'none'
  WVJBIframe.src = 'wvjbscheme://__BRIDGE_LOADED__'
  document.documentElement.appendChild(WVJBIframe)
  setTimeout(() => {
    document.documentElement.removeChild(WVJBIframe)
  }, 0)
}

//Android 交互声明
function connectWebViewJavascriptBridgeANDROID (callback) {
  if (window.WebViewJavascriptBridge) {
    callback(WebViewJavascriptBridge)
  } else {
    document.addEventListener(
      'WebViewJavascriptBridgeReady',
      function () {
        callback(WebViewJavascriptBridge)
      },
      false
    )
  }
}

export default {
  //H5调用Native
  callHandler (name, data, callback) {
    //iOS的方法
    if (navigator && /(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
      connectWebViewJavascriptBridgeIOS(function (bridge) {
        bridge.callHandler(name, data, callback)
      })
    }
    //Android方法
    if (navigator && /(Android)/i.test(navigator.userAgent)) {
      connectWebViewJavascriptBridgeANDROID(function (bridge) {
        bridge.callHandler(name, data, callback)
      })
    }
  },
  //Native调用H5
  registerHandler (name, callback) {
    //iOS的方法
    if (navigator && /(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
      connectWebViewJavascriptBridgeIOS(function (bridge) {
        bridge.registerHandler(name, function (data, responseCallback) {
          callback(data, responseCallback)
        })
      })
    }
    //Android方法
    if (navigator && /(Android)/i.test(navigator.userAgent)) {
      connectWebViewJavascriptBridgeANDROID(function (bridge) {
        bridge.init(function (message, responseCallback) {
          if (responseCallback) {
            // responseCallback(data);
          }
        })
        bridge.registerHandler(name, function (data, responseCallback) {
          callback(data, responseCallback)
        })
      })
    }

  },

}

