package com.turkcell.rencar_pair.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.dto.LicenseStatusResponseDto
import com.turkcell.rencar_pair.data.repository.AuthRepository
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.LicenseRepository
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
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val licenseRepository: LicenseRepository
) : ViewModel() {

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
            ProfileContract.Intent.SignOutClicked        -> handleSignOutClicked()
        }
    }

    private fun handleSignOutClicked() {
        viewModelScope.launch {
            authRepository.logout()
            sendEffect(ProfileContract.Effect.NavigateToLogin)
        }
    }

    private fun loadProfile() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = authRepository.getMe()) {
                is AuthResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading   = false,
                            userName    = result.data.fullName,
                            phoneNumber = result.data.phone ?: ""
                        )
                    }
                    loadLicenseStatus()
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    private suspend fun loadLicenseStatus() {
        val result = licenseRepository.getStatus()
        if (result is AuthResult.Success) {
            _state.update { it.copy(license = result.data.toLicenseVerification()) }
        }
    }

    private fun LicenseStatusResponseDto.toLicenseVerification(): LicenseVerification =
        when (status) {
            "APPROVED"     -> LicenseVerification(isVerified = true, statusLabel = "Onaylı")
            "REJECTED"     -> LicenseVerification(isVerified = false, statusLabel = "Reddedildi")
            "UNDER_REVIEW" -> LicenseVerification(isVerified = false, statusLabel = "İnceleniyor")
            else           -> LicenseVerification(isVerified = false, statusLabel = "Yüklenmedi")
        }

    private fun sendEffect(effect: ProfileContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
