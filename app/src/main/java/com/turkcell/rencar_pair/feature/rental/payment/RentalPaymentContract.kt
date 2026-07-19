package com.turkcell.rencar_pair.feature.rental.payment

import com.turkcell.rencar_pair.data.network.dto.CardResponseDto

object RentalPaymentContract {

    enum class Method { WALLET, CARD, IYZICO }

    data class State(
        val rentalId: String = "",
        val brand: String = "",
        val model: String = "",
        val plate: String = "",
        val durationMinutes: Double = 0.0,
        val distanceKm: Double = 0.0,
        val startFee: Double = 0.0,
        val serviceFee: Double = 0.0,
        val totalPrice: Double = 0.0,
        val cards: List<CardResponseDto> = emptyList(),
        val selectedMethod: Method = Method.WALLET,
        val selectedCardId: String? = null,
        val isLoading: Boolean = false,
        val isPaying: Boolean = false,
        val errorMessage: String? = null,
        val showIyzicoDialog: Boolean = false,
        val showPaymentSuccessDialog: Boolean = false
    ) {
        val vehicleTitle: String get() = "$brand $model"
        val usageFee: Double get() = (totalPrice - startFee - serviceFee).coerceAtLeast(0.0)
        val canPay: Boolean get() = !isLoading && !isPaying &&
            (selectedMethod != Method.CARD || selectedCardId != null)
        val paymentMethodLabel: String get() = when (selectedMethod) {
            Method.WALLET -> "Cüzdan"
            Method.CARD   -> "Kart"
            Method.IYZICO -> "İyzico"
        }

        val formattedDuration: String get() = "${Math.round(durationMinutes)} dk"
        val formattedDistance: String get() = "${"%.1f".format(distanceKm).replace('.', ',')} km"
        val formattedUsageFee: String get() = "₺${"%.2f".format(usageFee).replace('.', ',')}"
        val formattedStartFee: String get() = "₺${"%.2f".format(startFee).replace('.', ',')}"
        val formattedServiceFee: String get() = "₺${"%.2f".format(serviceFee).replace('.', ',')}"
        val formattedTotalPrice: String get() = "₺${"%.2f".format(totalPrice).replace('.', ',')}"
        val payButtonLabel: String get() = "$formattedTotalPrice Öde"
    }

    sealed interface Intent {
        data class MethodSelected(val method: Method)            : Intent
        data class CardSelected(val cardId: String)              : Intent
        data object PayClicked                                   : Intent
        data class IyzicoPaymentSucceeded(val paymentId: String) : Intent
        data class IyzicoPaymentFailed(val reason: String)       : Intent
        data object IyzicoPaymentCancelled                       : Intent
        data object PaymentSuccessDialogConfirmed                : Intent
    }

    sealed interface Effect {
        data class NavigateToHistory(val rentalId: String) : Effect
    }
}
