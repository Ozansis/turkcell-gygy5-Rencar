package com.turkcell.rencar_pair.feature.auth.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ConfirmationViewModel : ViewModel() {

    private val _state = MutableStateFlow(ConfirmationContract.State())
    val state: StateFlow<ConfirmationContract.State> = _state.asStateFlow()

    private val _effect = Channel<ConfirmationContract.Effect>(Channel.BUFFERED)
    val effect: Flow<ConfirmationContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: ConfirmationContract.Intent) {
        when (intent) {
            ConfirmationContract.Intent.Continue     -> sendEffect(ConfirmationContract.Effect.NavigateToHome)
            ConfirmationContract.Intent.NavigateBack -> sendEffect(ConfirmationContract.Effect.NavigateBack)
        }
    }

    private fun sendEffect(effect: ConfirmationContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
