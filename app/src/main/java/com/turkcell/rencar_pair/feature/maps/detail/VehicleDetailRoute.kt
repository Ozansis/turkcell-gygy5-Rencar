package com.turkcell.rencar_pair.feature.maps.detail

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VehicleDetailRoute(
    vehicleId: String,
    distanceMeters: Int,
    onNavigateBack: () -> Unit = {},
    viewModel: VehicleDetailViewModel = hiltViewModel<VehicleDetailViewModel, VehicleDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(vehicleId, distanceMeters) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                VehicleDetailContract.Effect.NavigateBack             -> onNavigateBack()
                VehicleDetailContract.Effect.ShowReservationConfirmed -> Unit
                VehicleDetailContract.Effect.ShowUnlockConfirmed      -> Unit
                is VehicleDetailContract.Effect.ShowError             -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    VehicleDetailScreen(state = state, onIntent = viewModel::onIntent)
}
