package com.example.ui.webview

import android.content.Context
import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.example.MainActivity

class WebInterface(
    private val context: Context,
    private val onShare: (String) -> Unit,
    private val onBiometric: () -> Unit,
    private val onScanQR: () -> Unit
) {

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun share(text: String) {
        onShare(text)
    }

    @JavascriptInterface
    fun authenticate() {
        onBiometric()
    }

    @JavascriptInterface
    fun scanQR() {
        onScanQR()
    }

    @JavascriptInterface
    fun sendNotification(title: String, message: String) {
        (context as? MainActivity)?.sendNativeNotification(title, message)
    }

    @JavascriptInterface
    fun playMusic(url: String, title: String) {
        (context as? MainActivity)?.playNativeMusic(url, title)
    }
}
