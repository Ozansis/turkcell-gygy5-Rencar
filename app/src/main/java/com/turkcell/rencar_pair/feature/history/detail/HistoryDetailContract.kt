package com.turkcell.rencar_pair.feature.history.detail

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

object HistoryDetailContract {

    data class State(
        val rentalId: String = "",
        val plate: String = "",
        val brand: String = "",
        val model: String = "",
        val type: String = "",
        val plan: String = "",
        val startedAt: String = "",
        val endedAt: String? = null,
        val distanceKm: Double = 0.0,
        val durationMinutes: Double = 0.0,
        val totalPrice: Double? = null,
        val startFee: Double = 0.0,
        val serviceFee: Double? = null,
        val discountAmount: Double = 0.0,
        val status: String = "",
        val paymentStatus: String = "",
        val paymentMethod: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) {
        val vehicleTitle: String get() = "$brand $model"
        val planLabel: String get() = when (plan) {
            "PER_MINUTE" -> "Dakikalık"
            "HOURLY"     -> "Saatlik"
            "DAILY"      -> "Günlük"
            else         -> plan
        }
        val statusLabel: String get() = when (status) {
            "COMPLETED" -> "Tamamlandı"
            "CANCELLED" -> "İptal Edildi"
            "ACTIVE"    -> "Devam Ediyor"
            "PREPARING" -> "Hazırlanıyor"
            else        -> status
        }
        val formattedStartedAt: String get() = runCatching {
            DATE_FORMATTER.format(Instant.parse(startedAt))
        }.getOrDefault(startedAt)
        val formattedEndedAt: String get() = endedAt?.let { raw ->
            runCatching { DATE_FORMATTER.format(Instant.parse(raw)) }.getOrDefault(raw)
        } ?: "—"
        val formattedDistance: String get() = "${"%.1f".format(distanceKm).replace('.', ',')} km"
        val formattedDuration: String get() = "${Math.round(durationMinutes)} dk"
        val formattedTotalPrice: String get() = totalPrice?.let { "₺${"%.2f".format(it).replace('.', ',')}" } ?: "—"
        val formattedStartFee: String get() = "₺${"%.2f".format(startFee).replace('.', ',')}"
        val formattedServiceFee: String get() = serviceFee?.let { "₺${"%.2f".format(it).replace('.', ',')}" } ?: "—"
        val formattedDiscount: String get() = if (discountAmount > 0) "-₺${"%.2f".format(discountAmount).replace('.', ',')}" else "—"
    }

    sealed interface Intent {
        data object NavigateBack : Intent
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}
