package com.example.ui.webview

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WebViewState(
    val url: String = "",
    val isLoading: Boolean = true,
    val progress: Int = 0,
    val error: String? = null,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isOffline: Boolean = false,
    val musicUrl: String? = null,
    val musicTitle: String? = null,
    val isPlaying: Boolean = false
)

class WebViewViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WebViewState())
    val uiState = _uiState.asStateFlow()

    fun playMusic(url: String, title: String) {
        _uiState.update { it.copy(musicUrl = url, musicTitle = title, isPlaying = true) }
    }

    fun stopMusic() {
        _uiState.update { it.copy(musicUrl = null, musicTitle = null, isPlaying = false) }
    }

    fun setPlaying(isPlaying: Boolean) {
        _uiState.update { it.copy(isPlaying = isPlaying) }
    }

    fun updateLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun updateProgress(progress: Int) {
        _uiState.update { it.copy(progress = progress) }
    }

    fun updateError(error: String?) {
        _uiState.update { it.copy(error = error, isLoading = false) }
    }

    fun updateNavigation(canGoBack: Boolean, canGoForward: Boolean) {
        _uiState.update { it.copy(canGoBack = canGoBack, canGoForward = canGoForward) }
    }

    fun updateUrl(url: String) {
        _uiState.update { it.copy(url = url, error = null, isOffline = false) }
    }

    fun updateOffline(isOffline: Boolean) {
        _uiState.update { it.copy(isOffline = isOffline, isLoading = false) }
    }
}
