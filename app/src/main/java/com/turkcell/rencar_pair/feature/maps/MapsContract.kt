package com.turkcell.rencar_pair.feature.maps

object MapsContract {

    data class State(
        val myLocation: GeoPoint? = null,
        val hasLocationPermission: Boolean = false,
        val vehicles: List<NearbyVehicle> = emptyList(),
        val selectedType: VehicleType? = null,
        val searchQuery: String = "",
        val selectedSegment: VehicleSegment? = null,
        val selectedTransmission: String? = null,
        val selectedMinSeats: Int? = null,
        val isFilterPanelExpanded: Boolean = false,
        val isLoading: Boolean = false,
        val activeRentalId: String? = null,
        val activeRentalVehicleLabel: String = ""
    ) {
        val filteredVehicles: List<NearbyVehicle> get() = vehicles
            .filter { selectedType == null || it.type == selectedType }
            .filter { selectedSegment == null || it.segment == selectedSegment }
            .filter { selectedTransmission == null || it.transmission == selectedTransmission }
            .filter { selectedMinSeats == null || it.seatCount >= selectedMinSeats }
            .filter {
                searchQuery.isBlank() ||
                    it.brand.contains(searchQuery, ignoreCase = true) ||
                    it.model.contains(searchQuery, ignoreCase = true) ||
                    it.plate.contains(searchQuery, ignoreCase = true)
            }

        val nearbyVehicleCount: Int get() = filteredVehicles.size

        /** Vites değerleri backend'de sabit bir enum değil; uydurmak yerine yüklenen veriden çıkarılır. */
        val availableTransmissions: List<String> get() =
            vehicles.map { it.transmission }.distinct().sorted()

        val activeExtraFilterCount: Int get() = listOfNotNull(
            selectedSegment,
            selectedTransmission,
            selectedMinSeats
        ).size
    }

    sealed interface Intent {
        data class LocationChanged(val location: GeoPoint)            : Intent
        data object LocationPermissionGranted                         : Intent
        data object LocationPermissionDenied                          : Intent
        data class TypeFilterSelected(val type: VehicleType?)         : Intent
        data class SearchQueryChanged(val value: String)               : Intent
        data class SegmentFilterSelected(val segment: VehicleSegment?) : Intent
        data class TransmissionFilterSelected(val transmission: String?) : Intent
        data class MinSeatsFilterSelected(val minSeats: Int?)          : Intent
        data object FilterPanelToggled                                 : Intent
        data object FiltersCleared                                     : Intent
        data class VehicleMarkerClicked(val vehicleId: String)        : Intent
        data object RecenterClicked                                   : Intent
        data object FindNearestClicked                                : Intent
        data object ActiveRentalBannerClicked                         : Intent
    }

    sealed interface Effect {
        data object RequestLocationRefresh                        : Effect
        data class NavigateToVehicleDetail(val vehicleId: String, val distanceMeters: Int) : Effect
        data class NavigateToActiveRental(val rentalId: String)   : Effect
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

enum class VehicleSegment { ECONOMY, COMFORT, SUV }

data class NearbyVehicle(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: VehicleType,
    val segment: VehicleSegment = VehicleSegment.ECONOMY, // yalnızca mini-harita tek marker akışlarında (VehicleDetail/ActiveRental) kullanılmıyor; Maps ekranı gerçek değeri her zaman açıkça set eder
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
