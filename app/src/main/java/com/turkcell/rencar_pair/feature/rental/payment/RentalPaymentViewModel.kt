package com.turkcell.rencar_pair.feature.rental.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.CardsRepository
import com.turkcell.rencar_pair.data.repository.RentalsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = RentalPaymentViewModel.Factory::class)
class RentalPaymentViewModel @AssistedInject constructor(
    @Assisted private val rentalId: String,
    private val rentalsRepository: RentalsRepository,
    private val cardsRepository: CardsRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(rentalId: String): RentalPaymentViewModel
    }

    private val _state = MutableStateFlow(RentalPaymentContract.State(rentalId = rentalId))
    val state: StateFlow<RentalPaymentContract.State> = _state.asStateFlow()

    private val _effect = Channel<RentalPaymentContract.Effect>(Channel.BUFFERED)
    val effect: Flow<RentalPaymentContract.Effect> = _effect.receiveAsFlow()

    init {
        loadPaymentSummary()
    }

    fun onIntent(intent: RentalPaymentContract.Intent) {
        when (intent) {
            is RentalPaymentContract.Intent.MethodSelected -> handleMethodSelected(intent.method)
            is RentalPaymentContract.Intent.CardSelected   -> handleCardSelected(intent.cardId)
            RentalPaymentContract.Intent.PayClicked        -> handlePayClicked()
        }
    }

    private fun loadPaymentSummary() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            coroutineScope {
                val rentalDeferred = async { rentalsRepository.getRental(rentalId) }
                val cardsDeferred  = async { cardsRepository.listCards() }
                val rentalResult = rentalDeferred.await()
                val cardsResult  = cardsDeferred.await()

                val cards = (cardsResult as? AuthResult.Success)?.data.orEmpty()
                val defaultCard = cards.firstOrNull { it.isDefault }

                when (rentalResult) {
                    is AuthResult.Success -> {
                        val rental = rentalResult.data
                        _state.update {
                            it.copy(
                                isLoading       = false,
                                brand           = rental.vehicle.brand,
                                model           = rental.vehicle.model,
                                plate           = rental.vehicle.plate,
                                durationMinutes = rental.durationMinutes,
                                distanceKm      = rental.distanceKm,
                                startFee        = rental.startFee,
                                serviceFee      = rental.serviceFee ?: 0.0,
                                totalPrice      = rental.totalPrice ?: 0.0,
                                cards           = cards,
                                selectedMethod  = if (defaultCard != null) {
                                    RentalPaymentContract.Method.CARD
                                } else {
                                    RentalPaymentContract.Method.WALLET
                                },
                                selectedCardId  = defaultCard?.id
                            )
                        }
                    }
                    is AuthResult.Error -> _state.update {
                        it.copy(isLoading = false, errorMessage = rentalResult.message)
                    }
                }
            }
        }
    }

    private fun handleMethodSelected(method: RentalPaymentContract.Method) {
        _state.update { current ->
            current.copy(
                selectedMethod = method,
                selectedCardId = if (method == RentalPaymentContract.Method.CARD) {
                    current.selectedCardId ?: current.cards.firstOrNull()?.id
                } else {
                    current.selectedCardId
                }
            )
        }
    }

    private fun handleCardSelected(cardId: String) {
        _state.update { it.copy(selectedCardId = cardId) }
    }

    private fun handlePayClicked() {
        val current = _state.value
        if (!current.canPay) return

        if (current.selectedMethod == RentalPaymentContract.Method.IYZICO) {
            sendEffect(RentalPaymentContract.Effect.ShowInfo("İyzico ile ödeme yakında eklenecek."))
            return
        }

        _state.update { it.copy(isPaying = true, errorMessage = null) }
        viewModelScope.launch {
            val cardId = if (current.selectedMethod == RentalPaymentContract.Method.CARD) {
                current.selectedCardId
            } else {
                null
            }
            when (val result = rentalsRepository.payRental(current.rentalId, current.selectedMethod.name, cardId)) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isPaying = false) }
                    sendEffect(RentalPaymentContract.Effect.NavigateToHistory(current.rentalId))
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isPaying = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun sendEffect(effect: RentalPaymentContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
