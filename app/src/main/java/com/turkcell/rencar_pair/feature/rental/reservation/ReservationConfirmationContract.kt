package com.turkcell.rencar_pair.feature.rental.reservation

object ReservationConfirmationContract {

    const val PREVIEW_MINUTES = 30

    enum class RentalPlan { PER_MINUTE, HOURLY, DAILY }

    data class State(
        val vehicleId: String = "",
        val brand: String = "",
        val model: String = "",
        val plate: String = "",
        val transmission: String = "",
        val seatCount: Int = 0,
        val fuelPercent: Int = 0,
        val pricePerMinute: Double = 0.0,
        val pricePerHour: Double = 0.0,
        val pricePerDay: Double = 0.0,
        val selectedPlan: RentalPlan = RentalPlan.PER_MINUTE,
        val startFee: Double = 0.0,
        val estimatedTotal: Double = 0.0,
        val previewMinutes: Int = PREVIEW_MINUTES,
        val isTermsAccepted: Boolean = false,
        val isLoadingVehicle: Boolean = false,
        val isLoadingQuote: Boolean = false,
        val isSubmitting: Boolean = false
    ) {
        val canComplete: Boolean get() = isTermsAccepted && !isSubmitting && !isLoadingQuote
        val formattedPricePerMinute: String get() = "₺${"%.2f".format(pricePerMinute).replace('.', ',')}/dk"
        val formattedPricePerHour: String get() = "₺${pricePerHour.toInt()}/sa"
        val formattedPricePerDay: String get() = "₺${pricePerDay.toInt()}"
        val formattedStartFee: String get() = "₺${"%.2f".format(startFee).replace('.', ',')}"
        val formattedEstimatedTotal: String get() = "~₺${"%.2f".format(estimatedTotal).replace('.', ',')}"
    }

    sealed interface Intent {
        data class PlanSelected(val plan: RentalPlan) : Intent
        data object TermsToggled                      : Intent
        data object CompleteReservationClicked         : Intent
        data object NavigateBack                       : Intent
    }

    sealed interface Effect {
        data object NavigateBack                                                       : Effect
        data class NavigateToActiveRental(val rentalId: String)                        : Effect
        data class NavigateToVehiclePhotos(val rentalId: String, val vehicleId: String) : Effect
        data class ShowError(val message: String)                                      : Effect
    }
}
