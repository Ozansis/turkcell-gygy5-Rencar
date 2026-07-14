package com.turkcell.rencar_pair.feature.auth.otp

object OtpContract {

    const val CODE_LENGTH = 6
    const val RESEND_COOLDOWN_SECONDS = 42

    data class State(
        val phoneNumber: String = "",
        val code: String = "",
        val remainingSeconds: Int = RESEND_COOLDOWN_SECONDS,
        val isVerifying: Boolean = false
    ) {
        val isCodeComplete: Boolean get() = code.length == CODE_LENGTH
        val canResend: Boolean get() = remainingSeconds <= 0
    }

    sealed interface Intent {
        data class CodeChanged(val value: String) : Intent
        data object Verify : Intent
        data object ResendCode : Intent
        data object ChangeNumber : Intent
    }

    sealed interface Effect {
        data object NavigateToHome : Effect
        data object NavigateBack : Effect
        data class ShowError(val message: String) : Effect
    }
}
