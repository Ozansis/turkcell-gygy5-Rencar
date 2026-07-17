package com.turkcell.rencar_pair.feature.auth.confirmation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ConfirmationRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToLicenseVerification: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ConfirmationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ConfirmationContract.Effect.NavigateToHome               -> onNavigateToHome()
                ConfirmationContract.Effect.NavigateToLicenseVerification -> onNavigateToLicenseVerification()
                ConfirmationContract.Effect.NavigateBack                 -> onNavigateBack()
            }
        }
    }

    ConfirmationScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
