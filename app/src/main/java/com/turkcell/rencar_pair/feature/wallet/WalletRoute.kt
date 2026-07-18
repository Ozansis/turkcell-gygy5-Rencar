package com.turkcell.rencar_pair.feature.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun WalletRoute(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var showAddBalanceSheet by remember { mutableStateOf(false) }
    var showAddCardSheet by remember { mutableStateOf(false) }
    var wasTopupSubmitting by remember { mutableStateOf(false) }
    var wasAddCardSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                WalletContract.Effect.ShowAddBalanceSheet -> showAddBalanceSheet = true
                WalletContract.Effect.ShowAddCardSheet    -> showAddCardSheet = true
            }
        }
    }

    LaunchedEffect(state.isTopupSubmitting) {
        if (wasTopupSubmitting && !state.isTopupSubmitting && state.errorMessage == null) {
            showAddBalanceSheet = false
        }
        wasTopupSubmitting = state.isTopupSubmitting
    }

    LaunchedEffect(state.isAddCardSubmitting) {
        if (wasAddCardSubmitting && !state.isAddCardSubmitting && state.errorMessage == null) {
            showAddCardSheet = false
        }
        wasAddCardSubmitting = state.isAddCardSubmitting
    }

    WalletScreen(state = state, onIntent = viewModel::onIntent)

    if (showAddBalanceSheet) {
        AddBalanceSheet(
            isSubmitting = state.isTopupSubmitting,
            errorMessage = state.errorMessage,
            onDismiss    = { showAddBalanceSheet = false },
            onConfirm    = { amount -> viewModel.onIntent(WalletContract.Intent.TopupConfirmed(amount)) }
        )
    }

    if (showAddCardSheet) {
        AddCardSheet(
            isSubmitting = state.isAddCardSubmitting,
            errorMessage = state.errorMessage,
            onDismiss    = { showAddCardSheet = false },
            onConfirm    = { brand, last4, expMonth, expYear ->
                viewModel.onIntent(WalletContract.Intent.AddCardConfirmed(brand, last4, expMonth, expYear))
            }
        )
    }
}
