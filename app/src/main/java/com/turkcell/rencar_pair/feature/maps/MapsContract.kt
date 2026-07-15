package com.turkcell.rencar_pair.feature.maps

object MapsContract {

    data class State(
        val myLocation: GeoPoint? = null,
        val hasLocationPermission: Boolean = false,
        val vehicles: List<NearbyVehicle> = emptyList(),
        val selectedType: VehicleType? = null,
        val isLoading: Boolean = false
    ) {
        val filteredVehicles: List<NearbyVehicle> get() =
            if (selectedType == null) vehicles else vehicles.filter { it.type == selectedType }

        val nearbyVehicleCount: Int get() = filteredVehicles.size
    }

    sealed interface Intent {
        data class LocationChanged(val location: GeoPoint)        : Intent
        data object LocationPermissionGranted                     : Intent
        data object LocationPermissionDenied                      : Intent
        data class TypeFilterSelected(val type: VehicleType?)     : Intent
        data class VehicleMarkerClicked(val vehicleId: String)    : Intent
        data object RecenterClicked                               : Intent
        data object FindNearestClicked                            : Intent
    }

    sealed interface Effect {
        data object RequestLocationRefresh                        : Effect
        data class NavigateToVehicleDetail(val vehicleId: String, val distanceMeters: Int) : Effect
        data object ShowLocationPermissionDeniedMessage           : Effect
        data class ShowError(val message: String)                 : Effect
    }
}

data class GeoPoint(
    val latitude: Double,
    val longitude: Double
)

enum class VehicleType { SEDAN, SUV, HATCHBACK, STATION, MINIVAN }

enum class VehicleStatus { AVAILABLE, RESERVED, RENTED, MAINTENANCE }

data class NearbyVehicle(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: VehicleType,
    val status: VehicleStatus,
    val pricePerDay: Double,
    val location: GeoPoint,
    val distanceMeters: Int,
    val fuelPercent: Int,
    val tankLabel: String,
    val rangeKm: Int,
    val transmission: String,
    val seatCount: Int,
    val pricePerMinute: Double,
    val pricePerHour: Double
)
