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

data class RentalPhotoDto(
    val side: String,
    val imageUrl: String,
    val createdAt: String
)

data class RentalPhotosStateDto(
    val rentalId: String,
    val photos: List<RentalPhotoDto>,
    val uploadedCount: Int,
    val remainingSides: List<String>,
    val photosComplete: Boolean
)

data class RentalStatsResponseDto(
    val month: String,
    val tripCount: Double,
    val totalSpent: Double,
    val totalMinutes: Double,
    val totalKm: Double
)

data class PayRentalDto(
    val method: String,
    val cardId: String? = null,
    val iyzicoPaymentId: String? = null
)

data class PaidCardSummaryDto(
    val brand: String,
    val last4: String
)

data class PayRentalResponseDto(
    val rentalId: String,
    val paymentStatus: String,
    val method: String,
    val totalPrice: Double,
    val discountAmount: Double,
    val paidAmount: Double,
    val walletBalance: Double?,
    val card: PaidCardSummaryDto?
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
