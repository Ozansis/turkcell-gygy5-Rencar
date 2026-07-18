package com.turkcell.rencar_pair.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ProfileRoute(
    onNavigateToLogin: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                // Kapsam dışı: Profili Düzenle/Ödeme Yöntemleri/Ayarlar/Yardım/Davet Et navigasyonu ayrı bir batch'te bağlanacak.
                ProfileContract.Effect.NavigateToEditProfile    -> Unit
                ProfileContract.Effect.NavigateToPaymentMethods -> Unit
                ProfileContract.Effect.NavigateToSettings       -> Unit
                ProfileContract.Effect.NavigateToHelp           -> Unit
                ProfileContract.Effect.NavigateToInvite         -> Unit
                ProfileContract.Effect.NavigateToLogin          -> onNavigateToLogin()
            }
        }
    }

    ProfileScreen(state = state, onIntent = viewModel::onIntent)
}
