package com.turkcell.rencar_pair.feature.auth.login

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

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginContract.State())
    val state: StateFlow<LoginContract.State> = _state.asStateFlow()

    private val _effect = Channel<LoginContract.Effect>(Channel.BUFFERED)
    val effect: Flow<LoginContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: LoginContract.Intent) {
        when (intent) {
            is LoginContract.Intent.PhoneNumberChanged -> handlePhoneNumberChanged(intent.value)
            LoginContract.Intent.SendCode               -> handleSendCode()
            LoginContract.Intent.GoToRegister           -> sendEffect(LoginContract.Effect.NavigateToRegister)
            LoginContract.Intent.NavigateBack           -> sendEffect(LoginContract.Effect.NavigateBack)
        }
    }

    private fun handlePhoneNumberChanged(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(10)
        _state.update { it.copy(phoneNumber = digitsOnly) }
    }

    private fun handleSendCode() {
        val current = _state.value
        if (!current.isPhoneNumberValid) return
        sendEffect(LoginContract.Effect.NavigateToOtp(current.phoneNumber))
    }

    private fun sendEffect(effect: LoginContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
