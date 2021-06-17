package com.geekcarl.android.webview.example.ui;

import android.util.Log;

import com.geekcarl.android.bridge.ErrorCode;
import com.geekcarl.android.bridge.OnBridgeCallBack;
import com.geekcarl.android.webview.GeekBridgeWebView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021/4/26
 * <p>
 *
 * @author zengxiangxin
 */
public class JavaTest {
    public static void test(GeekBridgeWebView webView) {
        webView.registerHandler("call", String.class, (data, callback) -> {
            Log.e("data", data);
            callback.invoke("back msg");
        });

        webView.callHandler("h5Handler",
                "11",
                String.class,
                new OnBridgeCallBack<String>() {
                    @Override
                    public void onError(@NotNull ErrorCode errorCode) {
                        Log.e("data", errorCode.toString());
                    }

                    @Override
                    public void onSuccess(@Nullable String data) {
                        Log.d("data", data);
                    }
                });
    }
}
