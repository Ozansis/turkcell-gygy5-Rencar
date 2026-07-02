package com.turkcell.rencar_pair.feature.auth.login

object LoginContract {

    data class State(
        val phoneNumber: String = "",
        val isLoading: Boolean = false
    ) {
        val isPhoneNumberValid: Boolean get() = phoneNumber.length == 10
    }

    sealed interface Intent {
        data class PhoneNumberChanged(val value: String) : Intent
        data object SendCode : Intent
        data object GoToRegister : Intent
        data object NavigateBack : Intent
    }

    sealed interface Effect {
        data class NavigateToOtp(val phoneNumber: String) : Effect
        data object NavigateToRegister : Effect
        data object NavigateBack : Effect
    }
}
