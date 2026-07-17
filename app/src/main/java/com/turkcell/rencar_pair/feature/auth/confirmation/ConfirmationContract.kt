package com.turkcell.rencar_pair.feature.auth.confirmation

object ConfirmationContract {

    data class State(
        val status: String? = null,
        val rejectReason: String? = null,
        val isLoading: Boolean = true
    ) {
        val isContinueEnabled: Boolean get() = status == "APPROVED"
        val isRejected: Boolean get() = status == "REJECTED"
        val isTerminal: Boolean get() = status == "APPROVED" || status == "REJECTED"
    }

    sealed interface Intent {
        data object Continue        : Intent
        data object ReuploadClicked : Intent
        data object NavigateBack    : Intent
    }

    sealed interface Effect {
        data object NavigateToHome                : Effect
        data object NavigateToLicenseVerification  : Effect
        data object NavigateBack                  : Effect
    }
}
