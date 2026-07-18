package com.turkcell.rencar_pair.data.network.dto

data class CardResponseDto(
    val id: String,
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int,
    val isDefault: Boolean,
    val createdAt: String
)

data class CreateCardDto(
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int
)
