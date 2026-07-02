package com.turkcell.rencar_pair.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileContract.Effect.NavigateToEditProfile    -> Unit
                ProfileContract.Effect.NavigateToPaymentMethods -> Unit
                ProfileContract.Effect.NavigateToSettings       -> Unit
                ProfileContract.Effect.NavigateToHelp           -> Unit
                ProfileContract.Effect.NavigateToInvite         -> Unit
                ProfileContract.Effect.NavigateToLogin          -> Unit
            }
        }
    }

    ProfileScreen(state = state, onIntent = viewModel::onIntent)
}
