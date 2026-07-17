package com.turkcell.rencar_pair.feature.rental.active

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.VehicleLocationSocketClient
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.RentalsRepository
import com.turkcell.rencar_pair.data.repository.VehiclesRepository
import com.turkcell.rencar_pair.feature.maps.GeoPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val POLL_INTERVAL_MS = 5000L
private const val MAX_ROUTE_POINTS = 300

@HiltViewModel(assistedFactory = ActiveRentalViewModel.Factory::class)
class ActiveRentalViewModel @AssistedInject constructor(
    @Assisted private val initialRentalId: String,
    private val rentalsRepository: RentalsRepository,
    private val vehiclesRepository: VehiclesRepository,
    private val vehicleLocationSocketClient: VehicleLocationSocketClient
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(rentalId: String): ActiveRentalViewModel
    }

    private val _state = MutableStateFlow(ActiveRentalContract.State(rentalId = initialRentalId))
    val state: StateFlow<ActiveRentalContract.State> = _state.asStateFlow()

    private val _effect = Channel<ActiveRentalContract.Effect>(Channel.BUFFERED)
    val effect: Flow<ActiveRentalContract.Effect> = _effect.receiveAsFlow()

    private var pollingJob: Job? = null
    private var isLocationObserverStarted = false

    init {
        startPolling()
    }

    fun onIntent(intent: ActiveRentalContract.Intent) {
        when (intent) {
            ActiveRentalContract.Intent.LockUnlockClicked   -> handleLockUnlockClicked()
            ActiveRentalContract.Intent.FinishRentalClicked -> handleFinishRentalClicked()
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            while (isActive) {
                refreshActiveRental()
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private suspend fun refreshActiveRental() {
        when (val result = rentalsRepository.getActiveRental()) {
            is AuthResult.Success -> {
                val rental = result.data
                _state.update {
                    it.copy(
                        isLoading      = false,
                        rentalId       = rental.id,
                        vehicleId      = rental.vehicleId,
                        brand          = rental.vehicle.brand,
                        model          = rental.vehicle.model,
                        plate          = rental.vehicle.plate,
                        plan           = rental.plan,
                        startFee       = rental.startFee,
                        startedAtIso   = rental.startedAt,
                        elapsedSeconds = rental.elapsedSeconds.toLong(),
                        currentCost    = rental.currentCost,
                        distanceKm     = rental.distanceKm
                    )
                }
                ensureVehicleLocationTracked(rental.vehicleId)
            }
            is AuthResult.Error -> {
                _state.update { it.copy(isLoading = false) }
                if (result.code == 404) {
                    pollingJob?.cancel()
                } else {
                    sendEffect(ActiveRentalContract.Effect.ShowError(result.message))
                }
            }
        }
    }

    private fun ensureVehicleLocationTracked(vehicleId: String) {
        if (isLocationObserverStarted) return
        isLocationObserverStarted = true
        viewModelScope.launch {
            when (val result = vehiclesRepository.getVehicle(vehicleId)) {
                is AuthResult.Success -> {
                    val vehicle = result.data
                    val initialLocation = GeoPoint(vehicle.latitude, vehicle.longitude)
                    _state.update {
                        it.copy(
                            vehicleLocation    = initialLocation,
                            vehiclePricePerDay = vehicle.pricePerDay,
                            routePoints        = it.routePoints + initialLocation
                        )
                    }
                }
                is AuthResult.Error -> Unit
            }
        }
        viewModelScope.launch {
            vehicleLocationSocketClient.vehicleLocationUpdates().collect { update ->
                if (update.vehicleId != vehicleId) return@collect
                _state.update { current ->
                    val nextPoints = if (current.routePoints.lastOrNull() == update.location) {
                        current.routePoints
                    } else {
                        (current.routePoints + update.location).takeLast(MAX_ROUTE_POINTS)
                    }
                    current.copy(vehicleLocation = update.location, routePoints = nextPoints)
                }
            }
        }
    }

    private fun handleLockUnlockClicked() {
        _state.update { it.copy(isUnlocked = true) }
        sendEffect(ActiveRentalContract.Effect.ShowInfo("Araç kilidi açıldı. Kiralamayı bitirebilirsiniz."))
    }

    private fun handleFinishRentalClicked() {
        val current = _state.value
        if (current.rentalId.isBlank() || !current.canFinish) return
        pollingJob?.cancel()
        _state.update { it.copy(isFinishing = true) }
        viewModelScope.launch {
            when (val result = rentalsRepository.finishRental(current.rentalId)) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isFinishing = false) }
                    sendEffect(ActiveRentalContract.Effect.NavigateToHome)
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isFinishing = false) }
                    sendEffect(ActiveRentalContract.Effect.ShowError(result.message))
                    startPolling()
                }
            }
        }
    }

    private fun sendEffect(effect: ActiveRentalContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    override fun onCleared() {
        pollingJob?.cancel()
    }
}
