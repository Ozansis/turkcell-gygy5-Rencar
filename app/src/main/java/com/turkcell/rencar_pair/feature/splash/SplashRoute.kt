package com.turkcell.rencar_pair.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun SplashRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashContract.Effect.NavigateToHome       -> onNavigateToHome()
                SplashContract.Effect.NavigateToOnboarding -> onNavigateToOnboarding()
                SplashContract.Effect.NavigateToLogin      -> onNavigateToLogin()
            }
        }
    }

    SplashScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
