package com.turkcell.rencar_pair.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OnboardingRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingContract.Effect.NavigateToHome  -> onNavigateToHome()
                OnboardingContract.Effect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    OnboardingScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
