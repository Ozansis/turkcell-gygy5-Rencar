package com.turkcell.rencar_pair.feature.splash

object SplashContract {

    data class State(val isLoading: Boolean = true)

    sealed interface Intent

    sealed interface Effect {
        data object NavigateToHome : Effect
        data object NavigateToOnboarding : Effect
        data object NavigateToLogin : Effect
        data object NavigateToLicenseVerification : Effect
        data object NavigateToConfirmation : Effect
    }
}
