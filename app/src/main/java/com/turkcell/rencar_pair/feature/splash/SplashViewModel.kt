package com.turkcell.rencar_pair.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.local.OnboardingPreferences
import com.turkcell.rencar_pair.data.local.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val MIN_SPLASH_DURATION_MS = 800L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenStore: TokenStore,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SplashContract.State())
    val state: StateFlow<SplashContract.State> = _state.asStateFlow()

    private val _effect = Channel<SplashContract.Effect>(Channel.BUFFERED)
    val effect: Flow<SplashContract.Effect> = _effect.receiveAsFlow()

    init {
        decideStartDestination()
    }

    fun onIntent(intent: SplashContract.Intent) {
    }

    private fun decideStartDestination() {
        viewModelScope.launch {
            val destinationEffect = coroutineScope {
                val minDelay = async { delay(MIN_SPLASH_DURATION_MS) }
                val destination = async { resolveDestination() }
                minDelay.await()
                destination.await()
            }
            sendEffect(destinationEffect)
        }
    }

    private suspend fun resolveDestination(): SplashContract.Effect {
        val hasValidToken = tokenStore.accessToken != null && tokenStore.readRefreshToken() != null
        return when {
            hasValidToken -> SplashContract.Effect.NavigateToHome
            !onboardingPreferences.hasSeenOnboarding() -> SplashContract.Effect.NavigateToOnboarding
            else -> SplashContract.Effect.NavigateToLogin
        }
    }

    private fun sendEffect(effect: SplashContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
