package com.turkcell.rencar_pair.feature.auth.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel


@Composable
fun RegisterRoute(
    onNavigateToLicenseVerification: () -> Unit,
    onNavigateToConfirmation: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterContract.Effect.NavigateToLicenseVerification -> onNavigateToLicenseVerification()
                RegisterContract.Effect.NavigateToConfirmation -> onNavigateToConfirmation()
                RegisterContract.Effect.NavigateToHome -> onNavigateToHome()
                RegisterContract.Effect.NavigateToLogin -> onNavigateToLogin()
                RegisterContract.Effect.NavigateBack -> onNavigateBack()
                is RegisterContract.Effect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RegisterScreen(
            state    = state,
            onIntent = viewModel::onIntent
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        )
    }
}
