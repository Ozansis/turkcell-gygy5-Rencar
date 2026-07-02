package com.turkcell.rencar_pair.feature.auth.selfie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SelfieRoute(
    onNavigateToConfirmation: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SelfieViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SelfieContract.Effect.NavigateToConfirmation -> onNavigateToConfirmation()
                SelfieContract.Effect.NavigateBack             -> onNavigateBack()
            }
        }
    }

    SelfieScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
