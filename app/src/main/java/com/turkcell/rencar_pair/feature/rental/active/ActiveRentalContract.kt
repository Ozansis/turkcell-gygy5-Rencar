package com.turkcell.rencar_pair.feature.rental.active

import com.turkcell.rencar_pair.feature.maps.GeoPoint

object ActiveRentalContract {

    data class State(
        val rentalId: String = "",
        val vehicleId: String = "",
        val brand: String = "",
        val model: String = "",
        val vehicleLocation: GeoPoint? = null,
        val vehiclePricePerDay: Double = 0.0,
        val elapsedSeconds: Long = 0,
        val currentCost: Double = 0.0,
        val distanceKm: Double = 0.0,
        val isLoading: Boolean = false,
        val isFinishing: Boolean = false
    ) {
        val statusLabel: String get() = "Kiralama aktif · $brand $model"
        val elapsedLabel: String get() {
            val h = elapsedSeconds / 3600
            val m = (elapsedSeconds % 3600) / 60
            val s = elapsedSeconds % 60
            return "%02d:%02d:%02d".format(h, m, s)
        }
        val formattedCurrentCost: String get() = "₺${"%.2f".format(currentCost).replace('.', ',')}"
        val formattedDistanceKm: String get() = "${"%.1f".format(distanceKm).replace('.', ',')} km"
    }

    sealed interface Intent {
        data object LockUnlockClicked   : Intent
        data object FinishRentalClicked : Intent
    }

    sealed interface Effect {
        data object NavigateToHome                : Effect
        data class ShowInfo(val message: String)   : Effect
        data class ShowError(val message: String)  : Effect
    }
}
