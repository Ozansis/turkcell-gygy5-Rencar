package com.turkcell.rencar_pair.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.dto.RentalResponseDto
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.RentalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val DATE_LABEL_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val rentalsRepository: RentalsRepository
) : ViewModel() {

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
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = rentalsRepository.listMine()) {
                is AuthResult.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        rentals = result.data
                            .filter { dto -> dto.status == "COMPLETED" }
                            .map { dto -> dto.toRentalRecord() }
                    )
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun RentalResponseDto.toRentalRecord(): RentalRecord = RentalRecord(
        id = id,
        carBrand = vehicle.brand,
        carModel = vehicle.model,
        dateLabel = formatDateLabel(startedAt),
        durationMinutes = Math.round(durationMinutes).toInt(),
        distanceKm = distanceKm,
        totalPrice = totalPrice ?: 0.0
    )

    private fun formatDateLabel(iso: String): String = runCatching {
        DATE_LABEL_FORMATTER.format(Instant.parse(iso))
    }.getOrDefault(iso)

    private fun sendEffect(effect: HistoryContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
