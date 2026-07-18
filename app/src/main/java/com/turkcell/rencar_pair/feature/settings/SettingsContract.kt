package com.turkcell.rencar_pair.feature.settings

import com.turkcell.rencar_pair.data.local.ThemeMode

object SettingsContract {

    data class State(
        val themeMode: ThemeMode = ThemeMode.SYSTEM
    )

    sealed interface Intent {
        data class ThemeModeSelected(val mode: ThemeMode) : Intent
        data object NavigateBack                          : Intent
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}
