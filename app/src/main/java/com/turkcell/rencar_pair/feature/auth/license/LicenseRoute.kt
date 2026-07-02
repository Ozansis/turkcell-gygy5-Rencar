package com.turkcell.rencar_pair.feature.auth.license

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LicenseRoute(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LicenseViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LicenseContract.Effect.NavigateToHome -> onNavigateToHome()
                LicenseContract.Effect.NavigateBack    -> onNavigateBack()
            }
        }
    }

    LicenseScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
