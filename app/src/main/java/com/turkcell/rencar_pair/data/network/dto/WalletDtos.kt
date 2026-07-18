package com.turkcell.rencar_pair.data.network.dto

data class WalletTransactionDto(
    val id: String,
    val type: String,
    val amount: Double,
    val rentalId: String?,
    val description: String,
    val createdAt: String
)

data class WalletResponseDto(
    val id: String,
    val balance: Double,
    val transactions: List<WalletTransactionDto>
)

data class TopupDto(
    val amount: Double
)
