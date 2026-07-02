package com.turkcell.rencar_pair.feature.auth.license

object LicenseContract {

    data class State(
        val isFrontUploaded: Boolean = true,
        val isBackUploaded: Boolean = false
    )

    sealed interface Intent {
        data object UploadBackSide : Intent
        data object Continue       : Intent
        data object NavigateBack   : Intent
    }

    sealed interface Effect {
        data object NavigateToHome : Effect
        data object NavigateBack   : Effect
    }
}
