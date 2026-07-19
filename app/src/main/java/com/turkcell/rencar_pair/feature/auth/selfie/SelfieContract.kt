package com.turkcell.rencar_pair.feature.auth.selfie

import android.net.Uri

object SelfieContract {

    data class State(
        val selfieUri: Uri? = null,
        val isUploading: Boolean = false,
        val uploadError: String? = null,
        val showLicenseSubmittedDialog: Boolean = false
    ) {
        val isContinueEnabled: Boolean get() = selfieUri != null && !isUploading
    }

    sealed interface Intent {
        data object CaptureSelfie                       : Intent
        data class SelfieImageSelected(val uri: Uri)     : Intent
        data object Continue                             : Intent
        data class UploadStateChanged(
            val isUploading: Boolean,
            val isUploaded: Boolean,
            val uploadError: String?
        ) : Intent
        data object LicenseSubmittedDialogConfirmed      : Intent
        data object NavigateBack                         : Intent
    }

    sealed interface Effect {
        data object LaunchCamera                  : Effect
        data object TriggerUpload                 : Effect
        data object NavigateToHome                : Effect
        data class ShowError(val message: String) : Effect
        data object NavigateBack                  : Effect
    }
}
