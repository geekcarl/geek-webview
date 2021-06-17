package com.geekcarl.android.webview.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.geekcarl.android.webview.R
import com.geekcarl.android.webview.X5WebView
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

/**
 * Created on 2020/6/23
 * @author zengxiangxin
 */
open class GeekX5WebChromeClient(
    mX5WebView: X5WebView,
    private var mFileChooser: IFileChooser?
) : LifecycleObserver,
    WebChromeClient() {
    private var mProgressAnimator: ObjectAnimator? = null
    var progressBar: ProgressBar?
        private set
    private var targetProgress = 0

    fun setFileChooser(fileChooser: IFileChooser) {
        mFileChooser = fileChooser
    }

    override fun onProgressChanged(
        webView: WebView,
        i: Int
    ) {
        super.onProgressChanged(webView, i)
        if (progressBar == null) {
            return
        }
        val progress = progressBar?.progress ?: 0
        val progressChanged = targetProgress != i
        if (progressChanged) {
            mProgressAnimator?.cancel()
        }
        val dis = i - progress
        if (dis > 0 && progressChanged) {
            targetProgress = i
            mProgressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress, i).apply {
                interpolator = DecelerateInterpolator()
                if (i == 100) {
                    duration = dis * 3.toLong()
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            progressBar!!.progress = 0
                            progressBar!!.visibility = View.GONE
                        }
                    })
                } else {
                    duration = dis * 10.toLong()
                }
            }
            mProgressAnimator?.start()
        }
    }

    /**
     * Tell the client to show a file chooser.
     *
     *
     * This is called to handle HTML forms with 'file' input type, in response to the
     * user pressing the "Select File" button.
     * To cancel the request, call `filePathCallback.onReceiveValue(null)` and
     * return `true`.
     *
     * @param webView           The WebView instance that is initiating the request.
     * @param filePathCallback  Invoke this callback to supply the list of paths to files to upload,
     * or `null` to cancel. Must only be called if the
     * [.onShowFileChooser] implementation returns `true`.
     * @param fileChooserParams Describes the mode of file chooser to be opened, and options to be
     * used with it.
     * @return `true` if filePathCallback will be invoked, `false` to use default
     * handling.
     * @see android.webkit.WebChromeClient.FileChooserParams
     */
    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        return mFileChooser?.let {
            it.onSelect(filePathCallback, fileChooserParams)
            true
        } ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onWebViewDestroy() {
        mProgressAnimator?.let {
            if (it.isStarted) {
                it.cancel()
            }
        }
        progressBar = null
    }

    init {
        progressBar = mX5WebView.findViewById(R.id.progress)
        if (progressBar == null) {
            val inflater = LayoutInflater.from(mX5WebView.context)
            inflater.inflate(R.layout.geeklib_webview_horizontal_progress_bar, mX5WebView)
            progressBar = mX5WebView.findViewById(R.id.progress)
        }

        if (mX5WebView.context is LifecycleOwner) {
            (mX5WebView.context as LifecycleOwner).lifecycle.addObserver(this)
        }
    }
}