package com.turkcell.rencar_pair.feature.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Shield
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

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        OnboardingContract.State(
            pages = listOf(
                OnboardingPageData(
                    icon     = Icons.Filled.DirectionsCar,
                    title    = "Rencar",
                    subtitle = "Yakındaki aracı bul,\ndakikalar içinde yola çık."
                ),
                OnboardingPageData(
                    icon     = Icons.Filled.Shield,
                    title    = "Güvenli & Hızlı",
                    subtitle = "Kimliğini bir kez doğrula,\nistediğin zaman kirala."
                )
            )
        )
    )
    val state: StateFlow<OnboardingContract.State> = _state.asStateFlow()

    private val _effect = Channel<OnboardingContract.Effect>(Channel.BUFFERED)
    val effect: Flow<OnboardingContract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: OnboardingContract.Intent) {
        when (intent) {
            OnboardingContract.Intent.PrimaryAction -> handlePrimaryAction()
            OnboardingContract.Intent.GoToLogin     -> sendEffect(OnboardingContract.Effect.NavigateToLogin)
        }
    }

    private fun handlePrimaryAction() {
        if (_state.value.isLastPage) {
            sendEffect(OnboardingContract.Effect.NavigateToHome)
        } else {
            _state.update { it.copy(currentPage = it.currentPage + 1) }
        }
    }

    private fun sendEffect(effect: OnboardingContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
