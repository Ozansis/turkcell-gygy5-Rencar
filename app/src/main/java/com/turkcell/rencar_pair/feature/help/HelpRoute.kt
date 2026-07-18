package com.turkcell.rencar_pair.feature.help

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HelpRoute(
    onNavigateBack: () -> Unit = {},
    viewModel: HelpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HelpContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    HelpScreen(state = state, onIntent = viewModel::onIntent)
}
