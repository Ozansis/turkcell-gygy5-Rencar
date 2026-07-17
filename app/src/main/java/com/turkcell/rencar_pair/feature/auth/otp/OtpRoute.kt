package com.turkcell.rencar_pair.feature.auth.otp

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun OtpRoute(
    phoneNumber: String,
    onNavigateToHome: () -> Unit,
    onNavigateToLicenseVerification: () -> Unit,
    onNavigateToConfirmation: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel<OtpViewModel, OtpViewModel.Factory>(
        creationCallback = { factory -> factory.create(phoneNumber) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OtpContract.Effect.NavigateToHome                -> onNavigateToHome()
                OtpContract.Effect.NavigateToLicenseVerification -> onNavigateToLicenseVerification()
                OtpContract.Effect.NavigateToConfirmation        -> onNavigateToConfirmation()
                OtpContract.Effect.NavigateBack                  -> onNavigateBack()
                is OtpContract.Effect.ShowError                  -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    OtpScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}
