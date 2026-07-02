package com.turkcell.rencar_pair.feature.auth.confirmation

object ConfirmationContract {

    data class State(val unit: Unit = Unit)

    sealed interface Intent {
        data object Continue     : Intent
        data object NavigateBack : Intent
    }

    sealed interface Effect {
        data object NavigateToHome : Effect
        data object NavigateBack   : Effect
    }
}
