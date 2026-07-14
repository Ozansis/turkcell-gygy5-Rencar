package com.turkcell.rencar_pair.feature.auth.register

import android.util.Patterns

object RegisterContract {

    data class State(
        val fullName: String = "",
        val email: String = "",
        val phoneNumber: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val referralCode: String = "",
        val isSubmitting: Boolean = false
    ) {
        val isFullNameValid: Boolean get() = fullName.isNotBlank()
        val isEmailValid: Boolean get() = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPhoneNumberValid: Boolean get() = phoneNumber.length == 10
        val isPasswordValid: Boolean get() = password.length >= 6
        val doPasswordsMatch: Boolean get() = password == confirmPassword
        val isFormValid: Boolean
            get() = isFullNameValid && isEmailValid && isPhoneNumberValid &&
                isPasswordValid && doPasswordsMatch
    }

    sealed interface Intent {
        data class FullNameChanged(val value: String) : Intent
        data class EmailChanged(val value: String) : Intent
        data class PhoneNumberChanged(val value: String) : Intent
        data class PasswordChanged(val value: String) : Intent
        data class ConfirmPasswordChanged(val value: String) : Intent
        data class ReferralCodeChanged(val value: String) : Intent
        data object Register : Intent
        data object GoToLogin : Intent
        data object NavigateBack : Intent
    }

    sealed interface Effect {
        data object NavigateToLicenseVerification : Effect
        data object NavigateToLogin : Effect
        data object NavigateBack : Effect
        data class ShowError(val message: String) : Effect
    }
}
