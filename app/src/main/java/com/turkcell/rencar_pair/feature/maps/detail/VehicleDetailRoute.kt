package com.turkcell.rencar_pair.feature.maps.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VehicleDetailRoute(
    vehicleId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: VehicleDetailViewModel = viewModel(
        factory = remember(vehicleId) { VehicleDetailViewModelFactory(vehicleId) }
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                VehicleDetailContract.Effect.NavigateBack             -> onNavigateBack()
                VehicleDetailContract.Effect.ShowReservationConfirmed -> Unit
                VehicleDetailContract.Effect.ShowUnlockConfirmed      -> Unit
            }
        }
    }

    VehicleDetailScreen(state = state, onIntent = viewModel::onIntent)
}

private class VehicleDetailViewModelFactory(private val vehicleId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = VehicleDetailViewModel(vehicleId) as T
}
