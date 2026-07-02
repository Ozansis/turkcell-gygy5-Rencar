package com.turkcell.rencar_pair.feature.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginRoute(
    onNavigateToOtp: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginContract.Effect.NavigateToOtp      -> onNavigateToOtp(effect.phoneNumber)
                LoginContract.Effect.NavigateToRegister    -> onNavigateToRegister()
                LoginContract.Effect.NavigateBack          -> onNavigateBack()
            }
        }
    }

    LoginScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
