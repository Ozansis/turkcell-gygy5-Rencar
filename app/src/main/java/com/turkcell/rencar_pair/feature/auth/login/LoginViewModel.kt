package com.turkcell.rencar_pair.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthRepository
import com.turkcell.rencar_pair.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val COUNTRY_CODE = "+90"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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
        if (!current.isPhoneNumberValid || current.isLoading) return

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = authRepository.requestOtp(COUNTRY_CODE + current.phoneNumber)) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(LoginContract.Effect.NavigateToOtp(current.phoneNumber))
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(LoginContract.Effect.ShowError(result.message))
                }
            }
        }
    }

    private fun sendEffect(effect: LoginContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
