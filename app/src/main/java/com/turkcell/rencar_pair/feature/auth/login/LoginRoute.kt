package com.turkcell.rencar_pair.feature.auth.login

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun LoginRoute(
    onNavigateToOtp: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginContract.Effect.NavigateToOtp      -> onNavigateToOtp(effect.phoneNumber)
                LoginContract.Effect.NavigateToRegister    -> onNavigateToRegister()
                LoginContract.Effect.NavigateBack          -> onNavigateBack()
                is LoginContract.Effect.ShowError          -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
