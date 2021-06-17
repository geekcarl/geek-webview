package com.geekcarl.android.webview.proxy

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.view.KeyEvent
import com.geekcarl.android.bridge.IBridgeLoader
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

class WebViewClientProxy(
    private var mClient: WebViewClient?
) : WebViewClient() {

    private var mBridgeLoader: IBridgeLoader? = null

    fun setBridgeLoader(loader: IBridgeLoader?) {
        mBridgeLoader = loader
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        url: String?
    ): Boolean {
        return mClient?.shouldOverrideUrlLoading(view, url)
            ?: super.shouldOverrideUrlLoading(view, url)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest?
    ): Boolean {
        return mClient?.shouldOverrideUrlLoading(view, request)
            ?: super.shouldOverrideUrlLoading(
                view,
                request
            )
    }

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: Bitmap?
    ) {
        mClient?.onPageStarted(view, url, favicon) ?: super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String?) {
        mClient?.onPageFinished(view, url)
            ?: super.onPageFinished(view, url)
        mBridgeLoader?.onBridgeLoad()
    }

    override fun onLoadResource(view: WebView, url: String?) {
        mClient?.onLoadResource(view, url)
            ?: super.onLoadResource(view, url)
    }

    override fun onPageCommitVisible(view: WebView, url: String?) {
        mClient?.onPageCommitVisible(view, url)
            ?: super.onPageCommitVisible(view, url)
    }

    override fun shouldInterceptRequest(
        view: WebView,
        url: String?
    ): WebResourceResponse? {
        return mClient?.shouldInterceptRequest(view, url)
            ?: super.shouldInterceptRequest(view, url)
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return mClient?.shouldInterceptRequest(view, request)
            ?: super.shouldInterceptRequest(view, request)
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest?,
        data: Bundle?
    ): WebResourceResponse {
        return mClient?.shouldInterceptRequest(view, request, data)
            ?: super.shouldInterceptRequest(view, request, data)
    }

    override fun onTooManyRedirects(
        view: WebView,
        cancelMsg: Message?,
        continueMsg: Message?
    ) {
        mClient?.onTooManyRedirects(view, cancelMsg, continueMsg)
            ?: super.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        mClient?.onReceivedError(view, errorCode, description, failingUrl)
            ?: super.onReceivedError(view, errorCode, description, failingUrl)
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        mClient?.onReceivedError(view, request, error)
            ?: super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        mClient?.onReceivedHttpError(view, request, errorResponse)
            ?: super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onFormResubmission(
        view: WebView,
        dontResend: Message?,
        resend: Message?
    ) {
        mClient?.onFormResubmission(view, dontResend, resend)
            ?: super.onFormResubmission(view, dontResend, resend)
    }

    override fun doUpdateVisitedHistory(
        view: WebView,
        url: String?,
        isReload: Boolean
    ) {
        mClient?.doUpdateVisitedHistory(view, url, isReload)
            ?: super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        mClient?.onReceivedSslError(view, handler, error)
            ?: super.onReceivedSslError(view, handler, error)
    }

    override fun onReceivedClientCertRequest(
        view: WebView,
        request: ClientCertRequest?
    ) {
        mClient?.onReceivedClientCertRequest(view, request)
            ?: super.onReceivedClientCertRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        mClient?.onReceivedHttpAuthRequest(view, handler, host, realm)
            ?: super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun shouldOverrideKeyEvent(
        view: WebView,
        event: KeyEvent?
    ): Boolean {
        return mClient?.shouldOverrideKeyEvent(view, event)
            ?: super.shouldOverrideKeyEvent(view, event)
    }

    override fun onUnhandledKeyEvent(
        view: WebView,
        event: KeyEvent?
    ) {
        mClient?.onUnhandledKeyEvent(view, event)
            ?: super.onUnhandledKeyEvent(view, event)
    }

    override fun onScaleChanged(
        view: WebView,
        oldScale: Float,
        newScale: Float
    ) {
        mClient?.onScaleChanged(view, oldScale, newScale)
            ?: super.onScaleChanged(view, oldScale, newScale)
    }

    override fun onReceivedLoginRequest(
        view: WebView,
        realm: String?,
        account: String?,
        args: String?
    ) {
        mClient?.onReceivedLoginRequest(view, realm, account, args)
            ?: super.onReceivedLoginRequest(view, realm, account, args)
    }

    override fun onRenderProcessGone(
        view: WebView,
        detail: a?
    ): Boolean {
        return mClient?.onRenderProcessGone(view, detail)
            ?: super.onRenderProcessGone(view, detail)
    }


    override fun onDetectedBlankScreen(p0: String?, p1: Int) {
        mClient?.onDetectedBlankScreen(p0, p1)
            ?: super.onDetectedBlankScreen(p0, p1)
    }
}