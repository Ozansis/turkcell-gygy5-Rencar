package com.turkcell.rencar_pair.data.network.dto

data class CreateReservationDto(
    val vehicleId: String
)

data class ReservationVehicleSummaryDto(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerMinute: Double
)

data class ReservationResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: ReservationVehicleSummaryDto,
    val status: String,
    val expiresAt: String,
    val remainingSeconds: Double,
    val createdAt: String
)
