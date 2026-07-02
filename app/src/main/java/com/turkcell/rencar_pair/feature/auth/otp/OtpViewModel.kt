package com.turkcell.rencar_pair.feature.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OtpViewModel(phoneNumber: String) : ViewModel() {

    private val _state = MutableStateFlow(OtpContract.State(phoneNumber = phoneNumber))
    val state: StateFlow<OtpContract.State> = _state.asStateFlow()

    private val _effect = Channel<OtpContract.Effect>(Channel.BUFFERED)
    val effect: Flow<OtpContract.Effect> = _effect.receiveAsFlow()

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    fun onIntent(intent: OtpContract.Intent) {
        when (intent) {
            is OtpContract.Intent.CodeChanged -> handleCodeChanged(intent.value)
            OtpContract.Intent.Verify          -> handleVerify()
            OtpContract.Intent.ResendCode      -> handleResendCode()
            OtpContract.Intent.ChangeNumber    -> sendEffect(OtpContract.Effect.NavigateBack)
        }
    }

    private fun handleCodeChanged(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(OtpContract.CODE_LENGTH)
        _state.update { it.copy(code = digitsOnly) }
    }

    private fun handleVerify() {
        if (!_state.value.isCodeComplete) return
        sendEffect(OtpContract.Effect.NavigateToHome)
    }

    private fun handleResendCode() {
        if (!_state.value.canResend) return
        _state.update { it.copy(code = "", remainingSeconds = OtpContract.RESEND_COOLDOWN_SECONDS) }
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_state.value.remainingSeconds > 0) {
                delay(1000)
                _state.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
        }
    }

    private fun sendEffect(effect: OtpContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
