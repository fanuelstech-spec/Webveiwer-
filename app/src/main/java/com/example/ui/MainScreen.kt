package com.example.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.webview.WebViewScreen
import com.example.ui.webview.WebViewViewModel

@Composable
fun MainScreen(viewModel: WebViewViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        WebViewScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
