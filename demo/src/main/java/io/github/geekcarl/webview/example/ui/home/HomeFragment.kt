package io.github.geekcarl.webview.example.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.geekcarl.bridge.*
import io.github.geekcarl.webview.example.R
import io.github.geekcarl.webview.example.databinding.FragmentHomeBinding
import io.github.geekcarl.webview.example.ui.TestObject

class HomeFragment : Fragment() {

    private val TAG = "WebViewBridge"
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeBinding: FragmentHomeBinding

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        homeBinding.bridgeWeb.loadUrl("file:///android_asset/h5/index.html")
        initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        return homeBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun initView() {

        homeBinding.setOnClick { v ->
            when (v?.id) {
                R.id.send1 -> this.onSend1()
                R.id.send2 -> this.send2()
                R.id.callFun1 -> this.callFun1()
                R.id.callFun2 -> this.callFun2()
                R.id.callHandler1 -> this.callHandler1()
                R.id.callHandler2 -> this.callHandler2()
                R.id.callHandlerNotFound -> this.callHandlerNotFound()
            }
        }
        val bridgeView = homeBinding.bridgeWeb

        bridgeView.registerHandler("callNative1", object : BridgeHandler<io.github.geekcarl.webview.example.ui.TestObject> {
            override fun hand(data: io.github.geekcarl.webview.example.ui.TestObject?, onFeedBack: OnBridgeFeedBack<Any>?) {
                Log.e(TAG, data.toString())
                show("callNative1", data)
                onFeedBack?.let { it(1000) }
            }
        })

        bridgeView.registerHandler("callNative2", object : BridgeHandler<String> {
            override fun hand(data: String?, onFeedBack: OnBridgeFeedBack<Any>?) {
                Log.e(TAG, data.toString())
                show("callNative2", data)
                onFeedBack?.let { it(
                    io.github.geekcarl.webview.example.ui.TestObject(
                        "哈哈",
                        "哈哈哈哈哈"
                    )
                ) }
            }
        })

        bridgeView.setDefaultHandler(object : BridgeHandler<Any> {
            override fun hand(data: Any?, onFeedBack: OnBridgeFeedBack<Any>?) {
                show("defaultHandler", data)
                onFeedBack?.let { it("我是Native默认回调") }
            }
        })
    }


    private fun show(handlerName: String, data: Any?) {
        homeBinding.content.text = "来自H5的call：$handlerName --->\n $data"
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun onSend1() {
//        homeBinding.bridgeWeb.callHandler<Any>(
//            "callH5Send1",
//            2222.23231231, null)

        homeBinding.bridgeWeb.sendData("111",
            object : OnBridgeCallBack<String> {
                override fun onError(errorCode: ErrorCode) {
                    show("sendData", errorCode)
                }

                override fun onSuccess(data: String?) {
                    show("sendData", data)
                }

            })
    }

    private fun send2() {
        homeBinding.bridgeWeb.callHandler<Float>(
            "callH5Send2",
            "我哦草",
            object : SimpleBridgeCallBack<Float>() {
                override fun onSuccess(data: Float?) {
                    show("callH5Send2", data)
                }
            })
    }

    private fun callFun1() {
        homeBinding.bridgeWeb.callFunc(
            "jsFunc1",
            "12121", 2323, "wewew"
        )
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun callFun2() {
        homeBinding.bridgeWeb.callFunc(
            "jsFunc2",
            object : ValueCallback<String?> {
                override fun invoke(data: String?) {
                    show("jsFunc2", data)
                }
            }, "1", 2, 3, 1.1
        )
    }

    private fun callHandler1() {
        homeBinding.bridgeWeb.callHandler(
            "callH5Handler1",
            io.github.geekcarl.webview.example.ui.TestObject("title", "This is Content"),
            object : SimpleBridgeCallBack<Array<*>>() {
                override fun onSuccess(data: Array<*>?) {
                    show("callH5Handler1", data)
                }
            })

//        homeBinding.bridgeWeb.callHandler(
//            "callH5Handler1",
//            TestObject("title", "This is Content"),
//            object : OnBridgeCallBack<Set<*>>{
//                override fun onError(errorCode: ErrorCode) {
//
//                }
//
//                override fun onSuccess(data: Set<*>?) {
//
//                }
//            })
    }

    private fun callHandler2() {
        homeBinding.bridgeWeb.callHandler(
            "callH5Handler2",
            3.14159f,
            object : SimpleBridgeCallBack<Map<*, *>>() {
                override fun onSuccess(data: Map<*, *>?) {
                    show("callH5Handler2", data)
                }
            })
    }

    private fun callHandlerNotFound() {
        homeBinding.bridgeWeb.callHandler(
            "callHandlerWocao",
            3.14159f,
            object : OnBridgeCallBack<Double> {
                override fun onSuccess(data: Double?) {
                    show("callHandlerWocao", data)
                }

                override fun onError(errorCode: ErrorCode) {
                    show("callHandlerWocao", errorCode)
                }
            })
    }
}