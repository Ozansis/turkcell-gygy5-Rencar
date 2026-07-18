package com.turkcell.rencar_pair.feature.history.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.dto.RentalResponseDto
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.RentalsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = HistoryDetailViewModel.Factory::class)
class HistoryDetailViewModel @AssistedInject constructor(
    @Assisted private val rentalId: String,
    private val rentalsRepository: RentalsRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(rentalId: String): HistoryDetailViewModel
    }

    private val _state = MutableStateFlow(HistoryDetailContract.State(rentalId = rentalId))
    val state: StateFlow<HistoryDetailContract.State> = _state.asStateFlow()

    private val _effect = Channel<HistoryDetailContract.Effect>(Channel.BUFFERED)
    val effect: Flow<HistoryDetailContract.Effect> = _effect.receiveAsFlow()

    init {
        loadRental()
    }

    fun onIntent(intent: HistoryDetailContract.Intent) {
        when (intent) {
            HistoryDetailContract.Intent.NavigateBack -> sendEffect(HistoryDetailContract.Effect.NavigateBack)
        }
    }

    private fun loadRental() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = rentalsRepository.getRental(rentalId)) {
                is AuthResult.Success -> _state.update { it.copy(isLoading = false).applyRental(result.data) }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun HistoryDetailContract.State.applyRental(rental: RentalResponseDto): HistoryDetailContract.State = copy(
        plate           = rental.vehicle.plate,
        brand           = rental.vehicle.brand,
        model           = rental.vehicle.model,
        type            = rental.vehicle.type,
        plan            = rental.plan,
        startedAt       = rental.startedAt,
        endedAt         = rental.endedAt,
        distanceKm      = rental.distanceKm,
        durationMinutes = rental.durationMinutes,
        totalPrice      = rental.totalPrice,
        startFee        = rental.startFee,
        serviceFee      = rental.serviceFee,
        discountAmount  = rental.discountAmount,
        status          = rental.status,
        paymentStatus   = rental.paymentStatus,
        paymentMethod   = rental.paymentMethod
    )

    private fun sendEffect(effect: HistoryDetailContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
