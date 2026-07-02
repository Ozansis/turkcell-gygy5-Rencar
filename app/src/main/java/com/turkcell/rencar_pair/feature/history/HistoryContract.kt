package com.turkcell.rencar_pair.feature.history

object HistoryContract {

    data class State(
        val rentals: List<RentalRecord> = emptyList(),
        val isLoading: Boolean = false
    ) {
        val monthlyTripCount: Int get() = rentals.size
        val monthlySpending: Double get() = rentals.sumOf { it.totalPrice }
    }

    sealed interface Intent {
        data class RentalSelected(val rentalId: String) : Intent
    }

    sealed interface Effect {
        data class NavigateToDetail(val rentalId: String) : Effect
    }
}
