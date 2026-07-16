package com.turkcell.rencar_pair.data.network.dto

data class VehicleResponseDto(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
    val pricePerDay: Double,
    val pricePerMinute: Double,
    val pricePerHour: Double,
    val fuelPercent: Double,
    val rangeKm: Double,
    val transmission: String,
    val seats: Int,
    val segment: String,
    val status: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: String,
    val updatedAt: String
)

data class QuoteResponseDto(
    val vehicleId: String,
    val plan: String,
    val minutes: Double,
    val usageFee: Double,
    val startFee: Double,
    val serviceFee: Double,
    val estimatedTotal: Double
)
