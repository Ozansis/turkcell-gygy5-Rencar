package com.turkcell.rencar_pair.feature.rental.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.RentalsRepository
import com.turkcell.rencar_pair.data.repository.ReservationsRepository
import com.turkcell.rencar_pair.data.repository.VehiclesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ReservationConfirmationViewModel.Factory::class)
class ReservationConfirmationViewModel @AssistedInject constructor(
    @Assisted private val vehicleId: String,
    private val vehiclesRepository: VehiclesRepository,
    private val reservationsRepository: ReservationsRepository,
    private val rentalsRepository: RentalsRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(vehicleId: String): ReservationConfirmationViewModel
    }

    private val _state = MutableStateFlow(ReservationConfirmationContract.State(vehicleId = vehicleId))
    val state: StateFlow<ReservationConfirmationContract.State> = _state.asStateFlow()

    private val _effect = Channel<ReservationConfirmationContract.Effect>(Channel.BUFFERED)
    val effect: Flow<ReservationConfirmationContract.Effect> = _effect.receiveAsFlow()

    private var countdownJob: Job? = null

    init {
        loadVehicle()
        loadQuote(ReservationConfirmationContract.RentalPlan.PER_MINUTE)
    }

    fun onIntent(intent: ReservationConfirmationContract.Intent) {
        when (intent) {
            is ReservationConfirmationContract.Intent.PlanSelected            -> handlePlanSelected(intent.plan)
            ReservationConfirmationContract.Intent.TermsToggled               -> handleTermsToggled()
            ReservationConfirmationContract.Intent.CompleteReservationClicked -> handleCompleteReservationClicked()
            ReservationConfirmationContract.Intent.NavigateBack               -> handleNavigateBack()
        }
    }

    private fun loadVehicle() {
        _state.update { it.copy(isLoadingVehicle = true) }
        viewModelScope.launch {
            when (val result = vehiclesRepository.getVehicle(vehicleId)) {
                is AuthResult.Success -> {
                    val vehicle = result.data
                    _state.update {
                        it.copy(
                            isLoadingVehicle = false,
                            brand           = vehicle.brand,
                            model           = vehicle.model,
                            plate           = vehicle.plate,
                            transmission    = vehicle.transmission,
                            seatCount       = vehicle.seats,
                            fuelPercent     = vehicle.fuelPercent.roundToInt(),
                            pricePerMinute  = vehicle.pricePerMinute,
                            pricePerHour    = vehicle.pricePerHour,
                            pricePerDay     = vehicle.pricePerDay
                        )
                    }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoadingVehicle = false) }
                    sendEffect(ReservationConfirmationContract.Effect.ShowError(result.message))
                }
            }
        }
    }

    private fun loadQuote(plan: ReservationConfirmationContract.RentalPlan) {
        _state.update { it.copy(isLoadingQuote = true) }
        viewModelScope.launch {
            when (val result = vehiclesRepository.getQuote(vehicleId, plan.name, ReservationConfirmationContract.PREVIEW_MINUTES)) {
                is AuthResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoadingQuote  = false,
                            startFee        = result.data.startFee,
                            estimatedTotal  = result.data.estimatedTotal
                        )
                    }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoadingQuote = false) }
                    sendEffect(ReservationConfirmationContract.Effect.ShowError(result.message))
                }
            }
        }
    }

    private fun handlePlanSelected(plan: ReservationConfirmationContract.RentalPlan) {
        if (_state.value.selectedPlan == plan) return
        _state.update { it.copy(selectedPlan = plan) }
        loadQuote(plan)
    }

    private fun handleTermsToggled() {
        _state.update { it.copy(isTermsAccepted = !it.isTermsAccepted) }
    }

    private fun handleCompleteReservationClicked() {
        val current = _state.value
        if (!current.canComplete) return
        _state.update { it.copy(isSubmitting = true) }
        if (current.reservationId != null) {
            // Hold zaten alınmış (önceki kiralama denemesi başarısız oldu); yeniden rezervasyon açmaya gerek yok.
            viewModelScope.launch { proceedToRental(current.selectedPlan) }
            return
        }
        viewModelScope.launch {
            when (val reservationResult = reservationsRepository.createReservation(vehicleId)) {
                is AuthResult.Success -> {
                    val reservation = reservationResult.data
                    _state.update {
                        it.copy(
                            reservationId = reservation.id,
                            remainingHoldSeconds = reservation.remainingSeconds.roundToInt()
                        )
                    }
                    startCountdown(reservation.remainingSeconds.roundToInt())
                    proceedToRental(current.selectedPlan)
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isSubmitting = false) }
                    sendEffect(ReservationConfirmationContract.Effect.ShowError(reservationResult.message))
                }
            }
        }
    }

    private suspend fun proceedToRental(plan: ReservationConfirmationContract.RentalPlan) {
        if (plan == ReservationConfirmationContract.RentalPlan.DAILY) {
            createDailyRental()
        } else {
            createUsageRental(plan)
        }
    }

    private suspend fun createDailyRental() {
        val endDate = Instant.now().plus(1, ChronoUnit.DAYS).toString()
        when (val rentalResult = rentalsRepository.createRental(vehicleId, "DAILY", endDate)) {
            is AuthResult.Success -> {
                markHoldConsumed()
                _state.update { it.copy(isSubmitting = false) }
                sendEffect(ReservationConfirmationContract.Effect.NavigateToActiveRental(rentalResult.data.id))
            }
            is AuthResult.Error -> {
                _state.update { it.copy(isSubmitting = false) }
                sendEffect(ReservationConfirmationContract.Effect.ShowError(rentalResult.message))
            }
        }
    }

    private suspend fun createUsageRental(plan: ReservationConfirmationContract.RentalPlan) {
        when (val rentalResult = rentalsRepository.createRental(vehicleId, plan.name)) {
            is AuthResult.Success -> {
                markHoldConsumed()
                _state.update { it.copy(isSubmitting = false) }
                sendEffect(ReservationConfirmationContract.Effect.NavigateToVehiclePhotos(rentalResult.data.id, vehicleId))
            }
            is AuthResult.Error -> {
                _state.update { it.copy(isSubmitting = false) }
                sendEffect(ReservationConfirmationContract.Effect.ShowError(rentalResult.message))
            }
        }
    }

    private fun markHoldConsumed() {
        countdownJob?.cancel()
        _state.update { it.copy(reservationId = null) }
    }

    private fun startCountdown(initialSeconds: Int) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var remaining = initialSeconds
            while (remaining > 0) {
                delay(1000)
                remaining -= 1
                _state.update { it.copy(remainingHoldSeconds = remaining) }
            }
            handleHoldExpired()
        }
    }

    private suspend fun handleHoldExpired() {
        val reservationId = _state.value.reservationId ?: return
        reservationsRepository.cancelReservation(reservationId)
        _state.update { it.copy(reservationId = null, isSubmitting = false) }
        sendEffect(ReservationConfirmationContract.Effect.ShowError("Rezervasyon süresi doldu, lütfen tekrar deneyin."))
        sendEffect(ReservationConfirmationContract.Effect.NavigateBack)
    }

    private fun handleNavigateBack() {
        val reservationId = _state.value.reservationId
        if (reservationId == null) {
            sendEffect(ReservationConfirmationContract.Effect.NavigateBack)
            return
        }
        countdownJob?.cancel()
        viewModelScope.launch {
            reservationsRepository.cancelReservation(reservationId)
            _state.update { it.copy(reservationId = null) }
            sendEffect(ReservationConfirmationContract.Effect.NavigateBack)
        }
    }

    private fun sendEffect(effect: ReservationConfirmationContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
