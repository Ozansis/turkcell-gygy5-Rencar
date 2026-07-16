package com.turkcell.rencar_pair.feature.rental.reservation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ReservationConfirmationRoute(
    vehicleId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToActiveRental: (String) -> Unit = {},
    viewModel: ReservationConfirmationViewModel = hiltViewModel<ReservationConfirmationViewModel, ReservationConfirmationViewModel.Factory>(
        creationCallback = { factory -> factory.create(vehicleId) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ReservationConfirmationContract.Effect.NavigateBack                    -> onNavigateBack()
                is ReservationConfirmationContract.Effect.NavigateToActiveRental       -> onNavigateToActiveRental(effect.rentalId)
                is ReservationConfirmationContract.Effect.NavigateBackWithMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                    onNavigateBack()
                }
                is ReservationConfirmationContract.Effect.ShowError                    -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    ReservationConfirmationScreen(state = state, onIntent = viewModel::onIntent)
}
