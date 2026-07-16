package com.turkcell.rencar_pair.data.network.dto

data class LicenseResponseDto(
    val id: String,
    val status: String,
    val frontImageUrl: String,
    val backImageUrl: String,
    val selfieImageUrl: String?,
    val rejectReason: String?,
    val reviewedAt: String?,
    val createdAt: String,
    val updatedAt: String
)

data class LicenseStatusResponseDto(
    val status: String,
    val frontImageUrl: String?,
    val backImageUrl: String?,
    val rejectReason: String?,
    val reviewedAt: String?
)
