package io.github.geekcarl.webview.base

import android.net.Uri
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient

/**
 * Created on 2020/6/8
 *
 * @author geekCarl
 */
interface IFileChooser {
    /**
     * 选择文件
     *
     * @param filePathCallback
     * @param fileChooserParams
     */
    fun onSelect(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    )
}