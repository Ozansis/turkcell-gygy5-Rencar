package com.turkcell.rencar_pair.feature.maps.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.feature.maps.MapsMockSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleDetailViewModel(private val vehicleId: String) : ViewModel() {

    private val _state = MutableStateFlow(VehicleDetailContract.State())
    val state: StateFlow<VehicleDetailContract.State> = _state.asStateFlow()

    private val _effect = Channel<VehicleDetailContract.Effect>(Channel.BUFFERED)
    val effect: Flow<VehicleDetailContract.Effect> = _effect.receiveAsFlow()

    init {
        loadVehicle()
    }

    fun onIntent(intent: VehicleDetailContract.Intent) {
        when (intent) {
            VehicleDetailContract.Intent.ReserveClicked -> handleReserveClicked()
            VehicleDetailContract.Intent.UnlockClicked  -> handleUnlockClicked()
            VehicleDetailContract.Intent.NavigateBack   -> sendEffect(VehicleDetailContract.Effect.NavigateBack)
        }
    }

    private fun loadVehicle() {
        val vehicle = MapsMockSource.vehicles.find { it.id == vehicleId } ?: return
        _state.update {
            it.copy(
                vehicleId      = vehicle.id,
                brand          = vehicle.brand,
                model          = vehicle.model,
                plate          = vehicle.plate,
                status         = vehicle.status,
                distanceMeters = vehicle.distanceMeters,
                fuelPercent    = vehicle.fuelPercent,
                tankLabel      = vehicle.tankLabel,
                rangeKm        = vehicle.rangeKm,
                transmission   = vehicle.transmission,
                seatCount      = vehicle.seatCount,
                pricePerMinute = vehicle.pricePerMinute,
                pricePerHour   = vehicle.pricePerHour
            )
        }
    }

    private fun handleReserveClicked() {
        if (!_state.value.canReserve) return
        sendEffect(VehicleDetailContract.Effect.ShowReservationConfirmed)
    }

    private fun handleUnlockClicked() {
        if (!_state.value.canUnlock) return
        sendEffect(VehicleDetailContract.Effect.ShowUnlockConfirmed)
    }

    private fun sendEffect(effect: VehicleDetailContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
