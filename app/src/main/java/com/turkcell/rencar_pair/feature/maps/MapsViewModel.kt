package com.turkcell.rencar_pair.feature.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.math.atan2
import kotlin.math.cos
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

class MapsViewModel : ViewModel() {

    private val _state = MutableStateFlow(MapsContract.State())
    val state: StateFlow<MapsContract.State> = _state.asStateFlow()

    private val _effect = Channel<MapsContract.Effect>(Channel.BUFFERED)
    val effect: Flow<MapsContract.Effect> = _effect.receiveAsFlow()

    init {
        loadVehicles()
    }

    fun onIntent(intent: MapsContract.Intent) {
        when (intent) {
            is MapsContract.Intent.LocationChanged        -> handleLocationChanged(intent.location)
            MapsContract.Intent.LocationPermissionGranted  -> handleLocationPermissionGranted()
            MapsContract.Intent.LocationPermissionDenied   -> handleLocationPermissionDenied()
            is MapsContract.Intent.TypeFilterSelected      -> handleTypeFilterSelected(intent.type)
            is MapsContract.Intent.VehicleMarkerClicked    -> sendEffect(MapsContract.Effect.NavigateToVehicleDetail(intent.vehicleId))
            MapsContract.Intent.RecenterClicked            -> sendEffect(MapsContract.Effect.RequestLocationRefresh)
            MapsContract.Intent.FindNearestClicked         -> handleFindNearestClicked()
        }
    }

    private fun loadVehicles() {
        _state.update { it.copy(vehicles = MapsMockSource.vehicles) }
    }

    private fun handleLocationChanged(location: GeoPoint) {
        _state.update { it.copy(myLocation = location) }
    }

    private fun handleLocationPermissionGranted() {
        _state.update { it.copy(hasLocationPermission = true) }
    }

    private fun handleLocationPermissionDenied() {
        _state.update { it.copy(hasLocationPermission = false) }
        sendEffect(MapsContract.Effect.ShowLocationPermissionDeniedMessage)
    }

    private fun handleTypeFilterSelected(type: VehicleType?) {
        _state.update { it.copy(selectedType = type) }
    }

    private fun handleFindNearestClicked() {
        val myLocation = _state.value.myLocation ?: return
        val nearest = _state.value.filteredVehicles
            .filter { it.status == VehicleStatus.AVAILABLE }
            .minByOrNull { distanceMeters(myLocation, it.location) }
            ?: return
        sendEffect(MapsContract.Effect.NavigateToVehicleDetail(nearest.id))
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
}
