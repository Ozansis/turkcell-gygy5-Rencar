package com.turkcell.rencar_pair.feature.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthRepository
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.domain.PostAuthDestination
import com.turkcell.rencar_pair.domain.PostAuthNavigationResolver
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
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

private const val COUNTRY_CODE = "+90"

@HiltViewModel(assistedFactory = OtpViewModel.Factory::class)
class OtpViewModel @AssistedInject constructor(
    @Assisted phoneNumber: String,
    private val authRepository: AuthRepository,
    private val postAuthNavigationResolver: PostAuthNavigationResolver
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(phoneNumber: String): OtpViewModel
    }

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
        val current = _state.value
        if (!current.isCodeComplete || current.isVerifying) return

        _state.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            val result = authRepository.verifyOtp(COUNTRY_CODE + current.phoneNumber, current.code)
            _state.update { it.copy(isVerifying = false) }
            when (result) {
                is AuthResult.Success -> sendEffect(resolvePostAuthEffect())
                is AuthResult.Error   -> sendEffect(OtpContract.Effect.ShowError(result.message))
            }
        }
    }

    private suspend fun resolvePostAuthEffect(): OtpContract.Effect {
        return when (postAuthNavigationResolver.resolve()) {
            PostAuthDestination.Home -> OtpContract.Effect.NavigateToHome
            PostAuthDestination.LicenseUpload -> OtpContract.Effect.NavigateToLicenseVerification
            PostAuthDestination.LicensePending -> OtpContract.Effect.NavigateToConfirmation
        }
    }

    private fun handleResendCode() {
        val current = _state.value
        if (!current.canResend) return

        _state.update { it.copy(code = "", remainingSeconds = OtpContract.RESEND_COOLDOWN_SECONDS) }
        startCountdown()
        viewModelScope.launch {
            val result = authRepository.requestOtp(COUNTRY_CODE + current.phoneNumber)
            if (result is AuthResult.Error) {
                sendEffect(OtpContract.Effect.ShowError(result.message))
            }
        }
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
