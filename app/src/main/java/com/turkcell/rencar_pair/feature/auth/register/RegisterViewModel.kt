package com.turkcell.rencar_pair.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthRepository
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.domain.PostAuthDestination
import com.turkcell.rencar_pair.domain.PostAuthNavigationResolver
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

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val postAuthNavigationResolver: PostAuthNavigationResolver
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterContract.State())
    val state: StateFlow<RegisterContract.State> = _state.asStateFlow()

    private val _effect = Channel<RegisterContract.Effect>(Channel.BUFFERED)
    val effect: Flow<RegisterContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterContract.Intent) {
        when (intent) {
            is RegisterContract.Intent.FullNameChanged -> handleFullNameChanged(intent.value)
            is RegisterContract.Intent.EmailChanged -> handleEmailChanged(intent.value)
            is RegisterContract.Intent.PhoneNumberChanged -> handlePhoneNumberChanged(intent.value)
            is RegisterContract.Intent.PasswordChanged -> handlePasswordChanged(intent.value)
            is RegisterContract.Intent.ConfirmPasswordChanged -> handleConfirmPasswordChanged(intent.value)
            is RegisterContract.Intent.ReferralCodeChanged -> handleReferralCodeChanged(intent.value)
            RegisterContract.Intent.Register -> handleRegister()
            RegisterContract.Intent.GoToLogin -> sendEffect(RegisterContract.Effect.NavigateToLogin)
            RegisterContract.Intent.NavigateBack -> sendEffect(RegisterContract.Effect.NavigateBack)
        }
    }

    private fun handleFullNameChanged(value: String) {
        _state.update { it.copy(fullName = value) }
    }

    private fun handleEmailChanged(value: String) {
        _state.update { it.copy(email = value) }
    }

    private fun handlePhoneNumberChanged(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(10)
        _state.update { it.copy(phoneNumber = digitsOnly) }
    }

    private fun handlePasswordChanged(value: String) {
        _state.update { it.copy(password = value) }
    }

    private fun handleConfirmPasswordChanged(value: String) {
        _state.update { it.copy(confirmPassword = value) }
    }

    private fun handleReferralCodeChanged(value: String) {
        _state.update { it.copy(referralCode = value) }
    }

    private fun handleRegister() {
        val current = _state.value
        if (!current.isFormValid || current.isSubmitting) return

        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = authRepository.register(
                email = current.email,
                password = current.password,
                fullName = current.fullName,
                phone = "+90${current.phoneNumber}",
                referralCode = current.referralCode.ifBlank { null }
            )
            _state.update { it.copy(isSubmitting = false) }
            when (result) {
                is AuthResult.Success -> sendEffect(resolvePostAuthEffect())
                is AuthResult.Error -> sendEffect(RegisterContract.Effect.ShowError(result.message))
            }
        }
    }

    private suspend fun resolvePostAuthEffect(): RegisterContract.Effect {
        return when (postAuthNavigationResolver.resolve()) {
            PostAuthDestination.Home -> RegisterContract.Effect.NavigateToHome
            PostAuthDestination.LicenseUpload -> RegisterContract.Effect.NavigateToLicenseVerification
            PostAuthDestination.LicensePending -> RegisterContract.Effect.NavigateToConfirmation
        }
    }

    private fun sendEffect(effect: RegisterContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
