package com.turkcell.rencar_pair.data.network.dto

data class InitializeCheckoutFormDto(
    val price: Double,
    val description: String? = null,
    val basketId: String? = null
)

data class CheckoutFormInitializeResponseDto(
    val status: String,
    val token: String,
    val tokenExpireTime: Int? = null,
    val paymentPageUrl: String? = null,
    val checkoutFormContent: String
)

data class IyzicoPaymentResponseDto(
    val status: String,
    val paymentId: String? = null,
    val conversationId: String? = null,
    val price: Double? = null,
    val paidPrice: Double? = null,
    val currency: String? = null,
    val installment: Int? = null,
    val paymentStatus: String? = null,
    val token: String? = null,
    val fraudStatus: Int? = null,
    val binNumber: String? = null,
    val lastFourDigits: String? = null,
    val cardType: String? = null,
    val cardAssociation: String? = null,
    val cardFamily: String? = null,
    val paymentTransactionIds: List<String>? = null
)
