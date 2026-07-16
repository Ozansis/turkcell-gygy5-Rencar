package com.turkcell.rencar_pair.data.network.dto

data class CreateRentalDto(
    val vehicleId: String,
    val plan: String? = null,
    val endDate: String? = null
)

data class RentalVehicleSummaryDto(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String
)

data class RentalResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummaryDto,
    val plan: String,
    val startedAt: String,
    val endedAt: String?,
    val endDate: String?,
    val totalPrice: Double?,
    val startFee: Double,
    val serviceFee: Double?,
    val distanceKm: Double,
    val durationMinutes: Double,
    val status: String,
    val paymentStatus: String,
    val paymentMethod: String?,
    val discountAmount: Double,
    val createdAt: String
)

data class ActiveRentalResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummaryDto,
    val plan: String,
    val startedAt: String,
    val endedAt: String?,
    val endDate: String?,
    val totalPrice: Double?,
    val startFee: Double,
    val serviceFee: Double?,
    val distanceKm: Double,
    val durationMinutes: Double,
    val status: String,
    val paymentStatus: String,
    val paymentMethod: String?,
    val discountAmount: Double,
    val createdAt: String,
    val elapsedSeconds: Double,
    val currentCost: Double
)
