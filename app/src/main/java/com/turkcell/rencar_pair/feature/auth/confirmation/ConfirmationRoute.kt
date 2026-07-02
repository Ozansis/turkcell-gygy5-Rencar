package com.turkcell.rencar_pair.feature.auth.confirmation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConfirmationRoute(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ConfirmationViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ConfirmationContract.Effect.NavigateToHome -> onNavigateToHome()
                ConfirmationContract.Effect.NavigateBack    -> onNavigateBack()
            }
        }
    }

    ConfirmationScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
