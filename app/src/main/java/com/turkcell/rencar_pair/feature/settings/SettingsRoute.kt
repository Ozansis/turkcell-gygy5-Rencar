package com.turkcell.rencar_pair.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SettingsContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    SettingsScreen(state = state, onIntent = viewModel::onIntent)
}
