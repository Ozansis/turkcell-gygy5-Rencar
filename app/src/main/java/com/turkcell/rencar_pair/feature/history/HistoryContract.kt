package com.turkcell.rencar_pair.feature.history

object HistoryContract {

    data class State(
        val rentals: List<RentalRecord> = emptyList(),
        val monthlyTripCount: Int = 0,
        val monthlySpending: Double = 0.0,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed interface Intent {
        data class RentalSelected(val rentalId: String) : Intent
    }

    sealed interface Effect {
        data class NavigateToDetail(val rentalId: String) : Effect
    }
}
