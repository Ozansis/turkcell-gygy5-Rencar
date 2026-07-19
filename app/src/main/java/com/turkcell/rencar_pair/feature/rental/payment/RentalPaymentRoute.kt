package com.turkcell.rencar_pair.feature.rental.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.turkcell.rencar_pair.ui.components.IyzicoPaymentWebViewRoute

@Composable
fun RentalPaymentRoute(
    rentalId: String,
    onNavigateToHistory: () -> Unit = {},
    viewModel: RentalPaymentViewModel = hiltViewModel<RentalPaymentViewModel, RentalPaymentViewModel.Factory>(
        creationCallback = { factory -> factory.create(rentalId) }
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RentalPaymentContract.Effect.NavigateToHistory -> onNavigateToHistory()
            }
        }
    }

    if (state.showIyzicoDialog) {
        IyzicoPaymentWebViewRoute(
            rentalId = rentalId,
            price = state.totalPrice,
            description = "${state.vehicleTitle} kiralama ödemesi",
            onPaymentSucceeded = { paymentId ->
                viewModel.onIntent(RentalPaymentContract.Intent.IyzicoPaymentSucceeded(paymentId))
            },
            onPaymentFailed = { reason ->
                viewModel.onIntent(RentalPaymentContract.Intent.IyzicoPaymentFailed(reason))
            },
            onPaymentCancelled = {
                viewModel.onIntent(RentalPaymentContract.Intent.IyzicoPaymentCancelled)
            }
        )
    }

    RentalPaymentScreen(state = state, onIntent = viewModel::onIntent)
}
