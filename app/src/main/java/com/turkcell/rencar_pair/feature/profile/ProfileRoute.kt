package com.turkcell.rencar_pair.feature.profile

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ProfileRoute(
    onNavigateToSettings: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToInvite: (String) -> Unit = {},
    onNavigateToLicenseVerification: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigateToSettings              -> onNavigateToSettings()
                ProfileContract.Effect.NavigateToHelp                  -> onNavigateToHelp()
                is ProfileContract.Effect.NavigateToInvite             -> onNavigateToInvite(effect.referralCode)
                ProfileContract.Effect.NavigateToLicenseVerification   -> onNavigateToLicenseVerification()
                ProfileContract.Effect.NavigateToLogin                 -> onNavigateToLogin()
                is ProfileContract.Effect.ShowToast                    -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    ProfileScreen(state = state, onIntent = viewModel::onIntent)
}
