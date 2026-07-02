package com.turkcell.rencar_pair.feature.profile

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

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileContract.State())
    val state: StateFlow<ProfileContract.State> = _state.asStateFlow()

    private val _effect = Channel<ProfileContract.Effect>(Channel.BUFFERED)
    val effect: Flow<ProfileContract.Effect> = _effect.receiveAsFlow()

    init {
        loadProfile()
    }

    fun onIntent(intent: ProfileContract.Intent) {
        when (intent) {
            ProfileContract.Intent.EditProfileClicked    -> sendEffect(ProfileContract.Effect.NavigateToEditProfile)
            ProfileContract.Intent.PaymentMethodsClicked -> sendEffect(ProfileContract.Effect.NavigateToPaymentMethods)
            ProfileContract.Intent.SettingsClicked       -> sendEffect(ProfileContract.Effect.NavigateToSettings)
            ProfileContract.Intent.HelpClicked           -> sendEffect(ProfileContract.Effect.NavigateToHelp)
            ProfileContract.Intent.InviteClicked         -> sendEffect(ProfileContract.Effect.NavigateToInvite)
            ProfileContract.Intent.SignOutClicked        -> sendEffect(ProfileContract.Effect.NavigateToLogin)
        }
    }

    private fun loadProfile() {
        _state.update {
            it.copy(
                userName    = ProfileMockSource.userName,
                phoneNumber = ProfileMockSource.phoneNumber,
                license     = ProfileMockSource.license
            )
        }
    }

    private fun sendEffect(effect: ProfileContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
