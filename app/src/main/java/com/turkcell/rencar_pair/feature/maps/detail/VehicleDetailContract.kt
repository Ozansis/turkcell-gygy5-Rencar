package com.turkcell.rencar_pair.feature.maps.detail

import com.turkcell.rencar_pair.feature.maps.GeoPoint
import com.turkcell.rencar_pair.feature.maps.VehicleStatus
import com.turkcell.rencar_pair.feature.maps.VehicleType

object VehicleDetailContract {

    data class State(
        val vehicleId: String = "",
        val brand: String = "",
        val model: String = "",
        val plate: String = "",
        val type: VehicleType = VehicleType.SEDAN,
        val status: VehicleStatus = VehicleStatus.AVAILABLE,
        val distanceMeters: Int = 0,
        val fuelPercent: Int = 0,
        val tankLabel: String = "",
        val rangeKm: Int = 0,
        val transmission: String = "",
        val seatCount: Int = 0,
        val pricePerMinute: Double = 0.0,
        val pricePerHour: Double = 0.0,
        val pricePerDay: Double = 0.0,
        val vehicleLocation: GeoPoint? = null,
        val myLocation: GeoPoint? = null,
        val isLoading: Boolean = false,
        val isUnlocked: Boolean = false
    ) {
        val canReserve: Boolean get() = status == VehicleStatus.AVAILABLE
        val canUnlock: Boolean get() = status == VehicleStatus.AVAILABLE
        val statusLabel: String get() = if (status == VehicleStatus.AVAILABLE) "MÜSAİT" else "MÜSAİT DEĞİL"
        val distanceLabel: String get() = "$distanceMeters m uzaklıkta"
        val formattedPricePerMinute: String get() = "₺${"%.2f".format(pricePerMinute).replace('.', ',')}/dk"
        val formattedPricePerHour: String get() = "Saatlik ₺${pricePerHour.toInt()}"
    }

    sealed interface Intent {
        data object ReserveClicked                          : Intent
        data object UnlockClicked                           : Intent
        data object NavigateBack                            : Intent
        data class LocationChanged(val location: GeoPoint)  : Intent
    }

    sealed interface Effect {
        data object NavigateBack             : Effect
        data object ShowReservationConfirmed : Effect
        data object ShowUnlockConfirmed      : Effect
        data class ShowError(val message: String) : Effect
    }
}
