package com.turkcell.rencar_pair.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val _state = MutableStateFlow(HistoryContract.State())
    val state: StateFlow<HistoryContract.State> = _state.asStateFlow()

    private val _effect = Channel<HistoryContract.Effect>(Channel.BUFFERED)
    val effect: Flow<HistoryContract.Effect> = _effect.receiveAsFlow()

    init {
        loadRentals()
    }

    fun onIntent(intent: HistoryContract.Intent) {
        when (intent) {
            is HistoryContract.Intent.RentalSelected ->
                sendEffect(HistoryContract.Effect.NavigateToDetail(intent.rentalId))
        }
    }

    private fun loadRentals() {
        _state.update { it.copy(rentals = HistoryMockSource.currentMonthRentals) }
    }

    private fun sendEffect(effect: HistoryContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
