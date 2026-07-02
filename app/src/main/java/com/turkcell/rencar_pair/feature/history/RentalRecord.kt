package com.turkcell.rencar_pair.feature.history

data class RentalRecord(
    val id: String,
    val carBrand: String,
    val carModel: String,
    val dateLabel: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val totalPrice: Double
)
