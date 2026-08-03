package com.example

import android.content.Intent
import android.net.Uri
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

    private val fileChooserLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        filePathCallback?.onReceiveValue(uris.toTypedArray())
        filePathCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
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
