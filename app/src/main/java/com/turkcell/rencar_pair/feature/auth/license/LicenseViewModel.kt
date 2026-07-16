package com.turkcell.rencar_pair.feature.auth.license

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

class LicenseViewModel : ViewModel() {

    private val _state = MutableStateFlow(LicenseContract.State())
    val state: StateFlow<LicenseContract.State> = _state.asStateFlow()

    private val _effect = Channel<LicenseContract.Effect>(Channel.BUFFERED)
    val effect: Flow<LicenseContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: LicenseContract.Intent) {
        when (intent) {
            LicenseContract.Intent.PickFrontImage        -> sendEffect(LicenseContract.Effect.ShowFrontImageSourceDialog)
            LicenseContract.Intent.PickBackImage         -> sendEffect(LicenseContract.Effect.ShowBackImageSourceDialog)
            is LicenseContract.Intent.FrontImageSelected -> handleFrontImageSelected(intent.uri)
            is LicenseContract.Intent.BackImageSelected  -> handleBackImageSelected(intent.uri)
            LicenseContract.Intent.Continue              -> handleContinue()
            LicenseContract.Intent.NavigateBack          -> sendEffect(LicenseContract.Effect.NavigateBack)
        }
    }

    private fun handleFrontImageSelected(uri: Uri) {
        _state.update { it.copy(frontUri = uri) }
    }

    private fun handleBackImageSelected(uri: Uri) {
        _state.update { it.copy(backUri = uri) }
    }

    private fun handleContinue() {
        if (!_state.value.isContinueEnabled) return
        sendEffect(LicenseContract.Effect.NavigateToHome)
    }

    private fun sendEffect(effect: LicenseContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
