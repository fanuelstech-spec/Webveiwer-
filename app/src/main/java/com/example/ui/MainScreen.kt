package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ui.components.NativeMusicPlayer
import com.example.ui.webview.WebViewScreen
import com.example.ui.webview.WebViewViewModel

@Composable
fun MainScreen(viewModel: WebViewViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            WebViewScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )

            uiState.musicUrl?.let { url ->
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    NativeMusicPlayer(
                        url = url,
                        title = uiState.musicTitle ?: "Unknown",
                        isPlaying = uiState.isPlaying,
                        onClose = { viewModel.stopMusic() },
                        onTogglePlay = { viewModel.setPlaying(it) }
                    )
                }
            }
        }
    }
}
