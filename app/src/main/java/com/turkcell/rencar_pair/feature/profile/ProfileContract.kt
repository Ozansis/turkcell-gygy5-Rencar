package com.turkcell.rencar_pair.feature.profile

object ProfileContract {

    const val COMING_SOON_MESSAGE = "Bu özellik yakında eklenecek"

    data class State(
        val userName: String = "",
        val phoneNumber: String = "",
        val referralCode: String? = null,
        val license: LicenseVerification? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Intent {
        data object EditProfileClicked      : Intent
        data object PaymentMethodsClicked   : Intent
        data object SettingsClicked         : Intent
        data object HelpClicked             : Intent
        data object InviteClicked           : Intent
        data object SignOutClicked          : Intent
    }

    sealed interface Effect {
        data object NavigateToSettings                        : Effect
        data object NavigateToHelp                             : Effect
        data class NavigateToInvite(val referralCode: String) : Effect
        data object NavigateToLogin                            : Effect
        data class ShowToast(val message: String)              : Effect
    }
}

data class LicenseVerification(
    val isVerified: Boolean,
    val statusLabel: String
)
