package com.turkcell.rencar_pair.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.local.ThemeMode
import com.turkcell.rencar_pair.data.local.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsContract.State())
    val state: StateFlow<SettingsContract.State> = _state.asStateFlow()

    private val _effect = Channel<SettingsContract.Effect>(Channel.BUFFERED)
    val effect: Flow<SettingsContract.Effect> = _effect.receiveAsFlow()

    init {
        observeThemeMode()
    }

    fun onIntent(intent: SettingsContract.Intent) {
        when (intent) {
            is SettingsContract.Intent.ThemeModeSelected -> handleThemeModeSelected(intent.mode)
            SettingsContract.Intent.NavigateBack         -> sendEffect(SettingsContract.Effect.NavigateBack)
        }
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            themePreferences.themeMode.collect { mode ->
                _state.update { it.copy(themeMode = mode) }
            }
        }
    }

    private fun handleThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { themePreferences.setThemeMode(mode) }
    }

    private fun sendEffect(effect: SettingsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
