package com.turkcell.rencar_pair.feature.auth.selfie

object SelfieContract {

    data class State(
        val isSelfieUploaded: Boolean = false
    )

    sealed interface Intent {
        data object UploadSelfie   : Intent
        data object Continue       : Intent
        data object NavigateBack   : Intent
    }

    sealed interface Effect {
        data object NavigateToConfirmation : Effect
        data object NavigateBack           : Effect
    }
}
