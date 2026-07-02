package com.turkcell.rencar_pair.feature.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WalletRoute(
    viewModel: WalletViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                WalletContract.Effect.ShowAddBalanceSheet -> Unit
                WalletContract.Effect.ShowAddCardSheet    -> Unit
            }
        }
    }

    WalletScreen(state = state, onIntent = viewModel::onIntent)
}
