package com.turkcell.rencar_pair.feature.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HelpViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HelpContract.State())
    val state: StateFlow<HelpContract.State> = _state.asStateFlow()

    private val _effect = Channel<HelpContract.Effect>(Channel.BUFFERED)
    val effect: Flow<HelpContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: HelpContract.Intent) {
        when (intent) {
            HelpContract.Intent.NavigateBack -> sendEffect(HelpContract.Effect.NavigateBack)
        }
    }

    private fun sendEffect(effect: HelpContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
