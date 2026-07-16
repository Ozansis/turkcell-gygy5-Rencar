package com.turkcell.rencar_pair.feature.auth.license

import android.net.Uri

object LicenseContract {

    data class State(
        val frontUri: Uri? = null,
        val backUri: Uri? = null
    ) {
        val isFrontUploaded: Boolean get() = frontUri != null
        val isBackUploaded: Boolean get() = backUri != null
        val isContinueEnabled: Boolean get() = isFrontUploaded && isBackUploaded
    }

    sealed interface Intent {
        data object PickFrontImage                 : Intent
        data object PickBackImage                  : Intent
        data class FrontImageSelected(val uri: Uri) : Intent
        data class BackImageSelected(val uri: Uri)  : Intent
        data object Continue                        : Intent
        data object NavigateBack                     : Intent
    }

    sealed interface Effect {
        data object ShowFrontImageSourceDialog : Effect
        data object ShowBackImageSourceDialog  : Effect
        data object NavigateToHome             : Effect
        data object NavigateBack               : Effect
    }
}
