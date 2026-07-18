package com.turkcell.rencar_pair.feature.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = InviteViewModel.Factory::class)
class InviteViewModel @AssistedInject constructor(
    @Assisted private val referralCode: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(referralCode: String): InviteViewModel
    }

    private val _state = MutableStateFlow(InviteContract.State(referralCode = referralCode))
    val state: StateFlow<InviteContract.State> = _state.asStateFlow()

    private val _effect = Channel<InviteContract.Effect>(Channel.BUFFERED)
    val effect: Flow<InviteContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: InviteContract.Intent) {
        when (intent) {
            InviteContract.Intent.ShareClicked -> handleShareClicked()
            InviteContract.Intent.NavigateBack -> sendEffect(InviteContract.Effect.NavigateBack)
        }
    }

    private fun handleShareClicked() {
        sendEffect(InviteContract.Effect.ShareReferralCode(referralCode))
    }

    private fun sendEffect(effect: InviteContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
