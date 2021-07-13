package io.github.geekcarl.webview.example.ui;

import android.util.Log;

import io.github.geekcarl.bridge.ErrorCode;
import io.github.geekcarl.bridge.OnBridgeCallBack;
import io.github.geekcarl.webview.GeekBridgeWebView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.geekcarl.bridge.ErrorCode;
import io.github.geekcarl.bridge.OnBridgeCallBack;
import io.github.geekcarl.webview.GeekBridgeWebView;

/**
 * Created on 2021/4/26
 * <p>
 *
 * @author geekCarl
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
