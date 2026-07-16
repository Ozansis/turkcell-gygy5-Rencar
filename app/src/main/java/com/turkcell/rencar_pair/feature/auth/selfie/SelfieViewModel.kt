package com.turkcell.rencar_pair.feature.auth.selfie

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelfieViewModel : ViewModel() {

    private val _state = MutableStateFlow(SelfieContract.State())
    val state: StateFlow<SelfieContract.State> = _state.asStateFlow()

    private val _effect = Channel<SelfieContract.Effect>(Channel.BUFFERED)
    val effect: Flow<SelfieContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: SelfieContract.Intent) {
        when (intent) {
            SelfieContract.Intent.CaptureSelfie          -> handleCaptureSelfie()
            is SelfieContract.Intent.SelfieImageSelected -> handleSelfieImageSelected(intent.uri)
            SelfieContract.Intent.Continue                -> handleContinue()
            is SelfieContract.Intent.UploadStateChanged   -> handleUploadStateChanged(intent.isUploading, intent.isUploaded, intent.uploadError)
            SelfieContract.Intent.NavigateBack            -> sendEffect(SelfieContract.Effect.NavigateBack)
        }
    }

    private fun handleCaptureSelfie() {
        if (_state.value.isUploading) return
        sendEffect(SelfieContract.Effect.LaunchCamera)
    }

    private fun handleSelfieImageSelected(uri: Uri) {
        _state.update { it.copy(selfieUri = uri) }
    }

    private fun handleContinue() {
        if (!_state.value.isContinueEnabled) return
        sendEffect(SelfieContract.Effect.TriggerUpload)
    }

    private fun handleUploadStateChanged(isUploading: Boolean, isUploaded: Boolean, uploadError: String?) {
        _state.update { it.copy(isUploading = isUploading, uploadError = uploadError) }
        if (isUploaded) {
            sendEffect(SelfieContract.Effect.NavigateToConfirmation)
        }
        if (uploadError != null) {
            sendEffect(SelfieContract.Effect.ShowError(uploadError))
        }
    }

    private fun sendEffect(effect: SelfieContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
