package com.turkcell.rencar_pair.feature.auth.selfie

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
            SelfieContract.Intent.UploadSelfie -> handleUploadSelfie()
            SelfieContract.Intent.Continue      -> handleContinue()
            SelfieContract.Intent.NavigateBack  -> sendEffect(SelfieContract.Effect.NavigateBack)
        }
    }

    private fun handleUploadSelfie() {
        _state.update { it.copy(isSelfieUploaded = true) }
    }

    private fun handleContinue() {
        sendEffect(SelfieContract.Effect.NavigateToConfirmation)
    }

    private fun sendEffect(effect: SelfieContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
