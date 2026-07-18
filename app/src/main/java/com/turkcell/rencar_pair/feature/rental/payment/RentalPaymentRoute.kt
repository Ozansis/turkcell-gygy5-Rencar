package com.turkcell.rencar_pair.feature.rental.payment

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun RentalPaymentRoute(
    rentalId: String,
    onNavigateToHistory: () -> Unit = {},
    viewModel: RentalPaymentViewModel = hiltViewModel<RentalPaymentViewModel, RentalPaymentViewModel.Factory>(
        creationCallback = { factory -> factory.create(rentalId) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RentalPaymentContract.Effect.NavigateToHistory -> onNavigateToHistory()
                is RentalPaymentContract.Effect.ShowInfo -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    RentalPaymentScreen(state = state, onIntent = viewModel::onIntent)
}
