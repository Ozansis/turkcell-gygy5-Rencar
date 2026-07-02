package com.turkcell.rencar_pair.feature.auth.otp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OtpRoute(
    phoneNumber: String,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: OtpViewModel = viewModel(
        factory = remember(phoneNumber) { OtpViewModelFactory(phoneNumber) }
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OtpContract.Effect.NavigateToHome -> onNavigateToHome()
                OtpContract.Effect.NavigateBack    -> onNavigateBack()
            }
        }
    }

    OtpScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}

private class OtpViewModelFactory(private val phoneNumber: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OtpViewModel(phoneNumber) as T
    }
}
