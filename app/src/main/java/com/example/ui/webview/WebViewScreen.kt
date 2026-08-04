package com.example.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.MainActivity
import com.example.data.AppConfig
import kotlinx.coroutines.launch
import android.app.DownloadManager
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import kotlinx.coroutines.delay
import androidx.browser.customtabs.CustomTabsIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    viewModel: WebViewViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var webView: WebView? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    BackHandler(enabled = uiState.canGoBack) {
        webView?.goBack()
    }

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                webView?.reload()
                // We'll reset isRefreshing in onPageFinished
                kotlinx.coroutines.delay(1000) // Safety timeout
                isRefreshing = false
            }
        },
        modifier = modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isOffline) {
                OfflineScreen(onRetry = {
                    viewModel.updateOffline(false)
                    webView?.reload()
                })
            } else {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webView = this
                            setupWebView(this, viewModel, ctx)
                            loadUrl(AppConfig.BASE_URL)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = {
                        // Update logic if needed
                    }
                )

                if (uiState.isLoading && !isRefreshing) {
                    LinearProgressIndicator(
                        progress = { uiState.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun setupWebView(webView: WebView, viewModel: WebViewViewModel, context: Context) {
    webView.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        builtInZoomControls = true
        displayZoomControls = false
        cacheMode = WebSettings.LOAD_DEFAULT
        allowFileAccess = true
        allowContentAccess = true
        javaScriptCanOpenWindowsAutomatically = true
        mediaPlaybackRequiresUserGesture = false
        
        // PWA and performance
        setSupportMultipleWindows(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            safeBrowsingEnabled = true
        }
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
    }

    webView.addJavascriptInterface(
        WebInterface(
            context = context,
            onShare = { text ->
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            },
            onBiometric = {
                (context as? MainActivity)?.showBiometricPrompt()
            },
            onScanQR = {
                (context as? MainActivity)?.startQRScanner()
            }
        ),
        "Android"
    )

    webView.webViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            viewModel.updateLoading(true)
            viewModel.updateUrl(url ?: "")
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            viewModel.updateLoading(false)
            viewModel.updateNavigation(view?.canGoBack() ?: false, view?.canGoForward() ?: false)
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            if (request?.isForMainFrame == true) {
                if (error?.errorCode == ERROR_HOST_LOOKUP || error?.errorCode == ERROR_CONNECT) {
                    viewModel.updateOffline(true)
                }
            }
            super.onReceivedError(view, request, error)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url.toString()
            
            // Handle PDF
            if (url.endsWith(".pdf", ignoreCase = true)) {
                val pdfUrl = "https://docs.google.com/viewer?url=$url"
                view?.loadUrl(pdfUrl)
                return true
            }

            // Handle native intents
            if (url.startsWith("tel:")) {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(url)))
                return true
            } else if (url.startsWith("mailto:")) {
                context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
                return true
            } else if (url.startsWith("sms:")) {
                context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
                return true
            } else if (url.startsWith("geo:")) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                return true
            }

            // Handle external domains
            if (AppConfig.EXTERNAL_DOMAINS.any { url.contains(it) }) {
                val customTabsIntent = CustomTabsIntent.Builder().build()
                customTabsIntent.launchUrl(context, Uri.parse(url))
                return true
            }

            return false
        }
    }

    webView.webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            viewModel.updateProgress(newProgress)
            super.onProgressChanged(view, newProgress)
        }

        override fun onPermissionRequest(request: PermissionRequest?) {
            request?.grant(request.resources)
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            (context as? MainActivity)?.handleFileChooser(filePathCallback, fileChooserParams)
            return true
        }
    }

    webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setMimeType(mimetype)
            addRequestHeader("User-Agent", userAgent)
            setDescription("Downloading file...")
            setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype))
        }
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun OfflineScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Internet Connection",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please check your network settings and try again.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}
