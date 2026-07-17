package com.turkcell.rencar_pair.feature.auth.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.LicenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val POLL_INTERVAL_MS = 5000L

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val licenseRepository: LicenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConfirmationContract.State())
    val state: StateFlow<ConfirmationContract.State> = _state.asStateFlow()

    private val _effect = Channel<ConfirmationContract.Effect>(Channel.BUFFERED)
    val effect: Flow<ConfirmationContract.Effect> = _effect.receiveAsFlow()

    private var pollingJob: Job? = null

    init {
        startPolling()
    }

    fun onIntent(intent: ConfirmationContract.Intent) {
        when (intent) {
            ConfirmationContract.Intent.Continue        -> handleContinue()
            ConfirmationContract.Intent.ReuploadClicked -> sendEffect(ConfirmationContract.Effect.NavigateToLicenseVerification)
            ConfirmationContract.Intent.NavigateBack    -> sendEffect(ConfirmationContract.Effect.NavigateBack)
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                refreshStatus()
                if (_state.value.isTerminal) break
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private suspend fun refreshStatus() {
        when (val result = licenseRepository.getStatus()) {
            is AuthResult.Success -> _state.update {
                it.copy(
                    isLoading    = false,
                    status       = result.data.status,
                    rejectReason = result.data.rejectReason
                )
            }
            is AuthResult.Error -> _state.update { it.copy(isLoading = false) }
        }
    }

    private fun handleContinue() {
        if (!_state.value.isContinueEnabled) return
        sendEffect(ConfirmationContract.Effect.NavigateToHome)
    }

    private fun sendEffect(effect: ConfirmationContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    override fun onCleared() {
        pollingJob?.cancel()
    }
}
