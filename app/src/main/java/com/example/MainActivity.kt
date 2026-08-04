package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.example.ui.MainScreen
import com.example.ui.theme.WebViewProTheme
import com.example.ui.webview.WebViewViewModel
import com.example.data.AppConfig

class MainActivity : FragmentActivity() {

    private val viewModel: WebViewViewModel by viewModels()
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var lastBackPressTime = 0L

    companion object {
        const val CHANNEL_ID = "webview_pro_notifications"
        const val CHANNEL_NAME = "Web Notifications"
    }

    private val fileChooserLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        filePathCallback?.onReceiveValue(uris.toTypedArray())
        filePathCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        createNotificationChannel()

        if (AppConfig.PREVENT_SCREENSHOTS) {
            window.setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }

        enableEdgeToEdge()
        setContent {
            WebViewProTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    fun handleFileChooser(callback: ValueCallback<Array<Uri>>?, params: WebChromeClient.FileChooserParams?) {
        filePathCallback = callback
        val mimeTypes = params?.acceptTypes?.filter { it.isNotEmpty() }?.toTypedArray() ?: arrayOf("*/*")
        fileChooserLauncher.launch(mimeTypes.firstOrNull() ?: "*/*")
    }

    fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                // You could notify the WebView here via evaluateJavascript
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun startQRScanner() {
        Toast.makeText(this, "QR Scanner triggered (Native Integration Ready)", Toast.LENGTH_SHORT).show()
        // Integration point for ML Kit or ZXing
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifications from the website"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNativeNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.welink_logo_1785800190811)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun playNativeMusic(url: String, title: String) {
        viewModel.playMusic(url, title)
        Toast.makeText(this, "Playing music: $title", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (viewModel.uiState.value.canGoBack) {
            // This is handled by BackHandler in WebViewScreen
            super.onBackPressed()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                super.onBackPressed()
            } else {
                lastBackPressTime = currentTime
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
