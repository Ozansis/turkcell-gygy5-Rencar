package com.turkcell.rencar_pair.feature.invite

object InviteContract {

    data class State(
        val referralCode: String = ""
    )

    sealed interface Intent {
        data object ShareClicked  : Intent
        data object NavigateBack  : Intent
    }

    sealed interface Effect {
        data class ShareReferralCode(val referralCode: String) : Effect
        data object NavigateBack                                : Effect
    }
}
