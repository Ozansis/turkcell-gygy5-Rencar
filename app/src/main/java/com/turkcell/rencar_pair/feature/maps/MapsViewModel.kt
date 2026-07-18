package com.turkcell.rencar_pair.feature.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.dto.VehicleResponseDto
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.RentalsRepository
import com.turkcell.rencar_pair.data.repository.VehiclesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val EARTH_RADIUS_METERS = 6_371_000.0
private const val FULL_TANK_THRESHOLD = 70.0
private const val HALF_TANK_THRESHOLD = 30.0

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val vehiclesRepository: VehiclesRepository,
    private val rentalsRepository: RentalsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapsContract.State())
    val state: StateFlow<MapsContract.State> = _state.asStateFlow()

    private val _effect = Channel<MapsContract.Effect>(Channel.BUFFERED)
    val effect: Flow<MapsContract.Effect> = _effect.receiveAsFlow()

    init {
        loadVehicles()
        loadActiveRental()
    }

    fun onIntent(intent: MapsContract.Intent) {
        when (intent) {
            is MapsContract.Intent.LocationChanged          -> handleLocationChanged(intent.location)
            MapsContract.Intent.LocationPermissionGranted    -> handleLocationPermissionGranted()
            MapsContract.Intent.LocationPermissionDenied     -> handleLocationPermissionDenied()
            MapsContract.Intent.LocationServicesEnabled      -> handleLocationServicesEnabled()
            MapsContract.Intent.LocationServicesDisabled     -> handleLocationServicesDisabled()
            MapsContract.Intent.PermissionRequestRetryClicked -> sendEffect(MapsContract.Effect.RequestLocationPermission)
            MapsContract.Intent.EnableLocationServicesClicked -> sendEffect(MapsContract.Effect.RequestEnableLocationServices)
            is MapsContract.Intent.TypeFilterSelected        -> handleTypeFilterSelected(intent.type)
            is MapsContract.Intent.SearchQueryChanged        -> handleSearchQueryChanged(intent.value)
            is MapsContract.Intent.SegmentFilterSelected     -> handleSegmentFilterSelected(intent.segment)
            is MapsContract.Intent.TransmissionFilterSelected -> handleTransmissionFilterSelected(intent.transmission)
            is MapsContract.Intent.MinSeatsFilterSelected    -> handleMinSeatsFilterSelected(intent.minSeats)
            MapsContract.Intent.FilterPanelToggled           -> handleFilterPanelToggled()
            MapsContract.Intent.FiltersCleared               -> handleFiltersCleared()
            is MapsContract.Intent.VehicleMarkerClicked      -> handleVehicleMarkerClicked(intent.vehicleId)
            MapsContract.Intent.RecenterClicked              -> sendEffect(MapsContract.Effect.RequestLocationRefresh)
            MapsContract.Intent.FindNearestClicked           -> handleFindNearestClicked()
            MapsContract.Intent.ActiveRentalBannerClicked    -> handleActiveRentalBannerClicked()
        }
    }

    private fun loadActiveRental() {
        viewModelScope.launch {
            when (val result = rentalsRepository.getActiveRental()) {
                is AuthResult.Success -> {
                    val rental = result.data
                    _state.update {
                        it.copy(
                            activeRentalId          = rental.id,
                            activeRentalVehicleLabel = "${rental.vehicle.brand} ${rental.vehicle.model}"
                        )
                    }
                }
                is AuthResult.Error -> Unit // Aktif kiralama yoksa (404) sessizce yok say.
            }
        }
    }

    private fun handleActiveRentalBannerClicked() {
        val rentalId = _state.value.activeRentalId ?: return
        sendEffect(MapsContract.Effect.NavigateToActiveRental(rentalId))
    }

    private fun loadVehicles() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = vehiclesRepository.listVehicles(includeBusy = true)) {
                is AuthResult.Success -> {
                    val myLocation = _state.value.myLocation
                    _state.update {
                        it.copy(
                            isLoading = false,
                            vehicles = result.data.mapNotNull { dto -> dto.toNearbyVehicle(myLocation) }
                        )
                    }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(MapsContract.Effect.ShowError(result.message))
                }
            }
        }
    }

    private fun handleLocationChanged(location: GeoPoint) {
        _state.update {
            it.copy(
                myLocation = location,
                vehicles = it.vehicles.map { vehicle ->
                    vehicle.copy(distanceMeters = distanceMeters(location, vehicle.location).roundToInt())
                }
            )
        }
    }

    private fun handleLocationPermissionGranted() {
        _state.update { it.copy(hasLocationPermission = true) }
    }

    private fun handleLocationPermissionDenied() {
        _state.update { it.copy(hasLocationPermission = false) }
        sendEffect(MapsContract.Effect.ShowLocationPermissionDeniedMessage)
    }

    private fun handleLocationServicesEnabled() {
        _state.update { it.copy(isLocationServiceEnabled = true) }
    }

    private fun handleLocationServicesDisabled() {
        _state.update { it.copy(isLocationServiceEnabled = false) }
    }

    private fun handleTypeFilterSelected(type: VehicleType?) {
        _state.update { it.copy(selectedType = type) }
    }

    private fun handleSearchQueryChanged(value: String) {
        _state.update { it.copy(searchQuery = value) }
    }

    private fun handleSegmentFilterSelected(segment: VehicleSegment?) {
        _state.update { it.copy(selectedSegment = segment) }
    }

    private fun handleTransmissionFilterSelected(transmission: String?) {
        _state.update { it.copy(selectedTransmission = transmission) }
    }

    private fun handleMinSeatsFilterSelected(minSeats: Int?) {
        _state.update { it.copy(selectedMinSeats = minSeats) }
    }

    private fun handleFilterPanelToggled() {
        _state.update { it.copy(isFilterPanelExpanded = !it.isFilterPanelExpanded) }
    }

    private fun handleFiltersCleared() {
        _state.update {
            it.copy(
                selectedType = null,
                selectedSegment = null,
                selectedTransmission = null,
                selectedMinSeats = null,
                searchQuery = ""
            )
        }
    }

    private fun handleVehicleMarkerClicked(vehicleId: String) {
        val vehicle = _state.value.vehicles.find { it.id == vehicleId } ?: return
        sendEffect(MapsContract.Effect.NavigateToVehicleDetail(vehicle.id, vehicle.distanceMeters))
    }

    private fun handleFindNearestClicked() {
        val myLocation = _state.value.myLocation ?: return
        val nearest = _state.value.filteredVehicles
            .filter { it.status == VehicleStatus.AVAILABLE }
            .minByOrNull { distanceMeters(myLocation, it.location) }
            ?: return
        sendEffect(MapsContract.Effect.NavigateToVehicleDetail(nearest.id, nearest.distanceMeters))
    }

    private fun distanceMeters(from: GeoPoint, to: GeoPoint): Double {
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    private fun sendEffect(effect: MapsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun VehicleResponseDto.toNearbyVehicle(myLocation: GeoPoint?): NearbyVehicle? {
        val vehicleType = runCatching { VehicleType.valueOf(type) }.getOrNull() ?: return null
        val vehicleStatus = runCatching { VehicleStatus.valueOf(status) }.getOrNull() ?: return null
        val vehicleSegment = runCatching { VehicleSegment.valueOf(segment) }.getOrNull() ?: return null
        val vehicleLocation = GeoPoint(latitude, longitude)
        return NearbyVehicle(
            id = id,
            plate = plate,
            brand = brand,
            model = model,
            type = vehicleType,
            segment = vehicleSegment,
            status = vehicleStatus,
            pricePerDay = pricePerDay,
            location = vehicleLocation,
            distanceMeters = myLocation?.let { distanceMeters(it, vehicleLocation).roundToInt() } ?: 0,
            fuelPercent = fuelPercent.roundToInt(),
            tankLabel = tankLabel(fuelPercent),
            rangeKm = rangeKm.roundToInt(),
            transmission = transmission,
            seatCount = seats,
            pricePerMinute = pricePerMinute,
            pricePerHour = pricePerHour
        )
    }

    private fun tankLabel(fuelPercent: Double): String = when {
        fuelPercent >= FULL_TANK_THRESHOLD -> "Dolu depo"
        fuelPercent >= HALF_TANK_THRESHOLD -> "Yarı dolu depo"
        else -> "Az yakıt"
    }
}
