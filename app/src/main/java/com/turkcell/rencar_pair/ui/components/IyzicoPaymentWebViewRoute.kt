package com.turkcell.rencar_pair.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.util.UUID

@Composable
fun IyzicoPaymentWebViewRoute(
    rentalId: String,
    price: Double,
    description: String? = null,
    onPaymentSucceeded: (paymentId: String) -> Unit = {},
    onPaymentFailed: (reason: String) -> Unit = {},
    onPaymentCancelled: () -> Unit = {},
    viewModel: IyzicoPaymentWebViewViewModel = hiltViewModel<IyzicoPaymentWebViewViewModel, IyzicoPaymentWebViewViewModel.Factory>(
        key = remember { UUID.randomUUID().toString() },
        creationCallback = { factory -> factory.create(rentalId, price, description) }
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is IyzicoPaymentWebViewContract.Effect.ShowPaymentSucceeded -> onPaymentSucceeded(effect.paymentId)
                is IyzicoPaymentWebViewContract.Effect.ShowPaymentFailed    -> onPaymentFailed(effect.reason)
                IyzicoPaymentWebViewContract.Effect.ShowPaymentCancelled   -> onPaymentCancelled()
            }
        }
    }

    Dialog(
        onDismissRequest = { viewModel.onIntent(IyzicoPaymentWebViewContract.Intent.Dismissed) },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false
        )
    ) {
        IyzicoPaymentWebViewScreen(state = state, onIntent = viewModel::onIntent)
    }
}
