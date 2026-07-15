package com.turkcell.rencar_pair.feature.maps.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.dto.VehicleResponseDto
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.VehiclesRepository
import com.turkcell.rencar_pair.feature.maps.VehicleStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.math.roundToInt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val FULL_TANK_THRESHOLD = 70.0
private const val HALF_TANK_THRESHOLD = 30.0

@HiltViewModel(assistedFactory = VehicleDetailViewModel.Factory::class)
class VehicleDetailViewModel @AssistedInject constructor(
    @Assisted private val vehicleId: String,
    @Assisted private val initialDistanceMeters: Int,
    private val vehiclesRepository: VehiclesRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(vehicleId: String, distanceMeters: Int): VehicleDetailViewModel
    }

    private val _state = MutableStateFlow(VehicleDetailContract.State(vehicleId = vehicleId, distanceMeters = initialDistanceMeters))
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
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = vehiclesRepository.getVehicle(vehicleId)) {
                is AuthResult.Success -> {
                    val vehicle = result.data
                    val vehicleStatus = runCatching { VehicleStatus.valueOf(vehicle.status) }.getOrNull()
                    _state.update {
                        it.copy(
                            isLoading      = false,
                            brand          = vehicle.brand,
                            model          = vehicle.model,
                            plate          = vehicle.plate,
                            status         = vehicleStatus ?: it.status,
                            fuelPercent    = vehicle.fuelPercent.roundToInt(),
                            tankLabel      = tankLabel(vehicle.fuelPercent),
                            rangeKm        = vehicle.rangeKm.roundToInt(),
                            transmission   = vehicle.transmission,
                            seatCount      = vehicle.seats,
                            pricePerMinute = vehicle.pricePerMinute,
                            pricePerHour   = vehicle.pricePerHour
                        )
                    }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(VehicleDetailContract.Effect.ShowError(result.message))
                }
            }
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

    private fun tankLabel(fuelPercent: Double): String = when {
        fuelPercent >= FULL_TANK_THRESHOLD -> "Dolu depo"
        fuelPercent >= HALF_TANK_THRESHOLD -> "Yarı dolu depo"
        else -> "Az yakıt"
    }
}
