package com.turkcell.rencar_pair.feature.rental.active

import com.turkcell.rencar_pair.feature.maps.GeoPoint
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val STARTED_AT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

object ActiveRentalContract {

    data class State(
        val rentalId: String = "",
        val vehicleId: String = "",
        val brand: String = "",
        val model: String = "",
        val plate: String = "",
        val plan: String = "",
        val startFee: Double = 0.0,
        val startedAtIso: String = "",
        val vehicleLocation: GeoPoint? = null,
        val routePoints: List<GeoPoint> = emptyList(),
        val vehiclePricePerDay: Double = 0.0,
        val elapsedSeconds: Long = 0,
        val currentCost: Double = 0.0,
        val distanceKm: Double = 0.0,
        val isLoading: Boolean = false,
        val isFinishing: Boolean = false,
        val isUnlocked: Boolean = false
    ) {
        val canFinish: Boolean get() = isUnlocked && !isFinishing
        val vehicleTitle: String get() = "$brand $model"
        val planLabel: String get() = when (plan) {
            "PER_MINUTE" -> "Dakikalık"
            "HOURLY"     -> "Saatlik"
            "DAILY"      -> "Günlük"
            else         -> plan
        }
        val vehicleSubtitle: String get() = "$plate · $planLabel"
        val elapsedLabel: String get() {
            val h = elapsedSeconds / 3600
            val m = (elapsedSeconds % 3600) / 60
            val s = elapsedSeconds % 60
            return "%02d:%02d:%02d".format(h, m, s)
        }
        val formattedStartedAt: String get() = runCatching {
            STARTED_AT_FORMATTER.format(Instant.parse(startedAtIso))
        }.getOrDefault("")
        val formattedCurrentCost: String get() = "₺${"%.2f".format(currentCost).replace('.', ',')}"
        val formattedDistanceKm: String get() = "${"%.1f".format(distanceKm).replace('.', ',')} km"
        val formattedStartFeeNote: String get() =
            "Anlık ücrete ${"%.0f".format(startFee)} ₺ başlangıç ücreti dahildir; kesin döküm bitirince çıkar."
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
