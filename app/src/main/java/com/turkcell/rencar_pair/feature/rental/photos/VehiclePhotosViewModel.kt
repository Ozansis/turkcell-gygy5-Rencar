package com.turkcell.rencar_pair.feature.rental.photos

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.dto.RentalPhotosStateDto
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.RentalsRepository
import com.turkcell.rencar_pair.data.repository.VehiclesRepository
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

@HiltViewModel(assistedFactory = VehiclePhotosViewModel.Factory::class)
class VehiclePhotosViewModel @AssistedInject constructor(
    @Assisted("rentalId") private val rentalId: String,
    @Assisted("vehicleId") private val vehicleId: String,
    private val rentalsRepository: RentalsRepository,
    private val vehiclesRepository: VehiclesRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("rentalId") rentalId: String, @Assisted("vehicleId") vehicleId: String): VehiclePhotosViewModel
    }

    private val _state = MutableStateFlow(VehiclePhotosContract.State(rentalId = rentalId, vehicleId = vehicleId))
    val state: StateFlow<VehiclePhotosContract.State> = _state.asStateFlow()

    private val _effect = Channel<VehiclePhotosContract.Effect>(Channel.BUFFERED)
    val effect: Flow<VehiclePhotosContract.Effect> = _effect.receiveAsFlow()

    init {
        loadVehicle()
        loadExistingPhotos()
    }

    fun onIntent(intent: VehiclePhotosContract.Intent) {
        when (intent) {
            is VehiclePhotosContract.Intent.CapturePhotoClicked -> sendEffect(VehiclePhotosContract.Effect.LaunchCamera(intent.side))
            is VehiclePhotosContract.Intent.PhotoCaptured        -> handlePhotoCaptured(intent.side, intent.uri)
            VehiclePhotosContract.Intent.StartRentalClicked      -> handleStartRentalClicked()
            VehiclePhotosContract.Intent.NavigateBack            -> sendEffect(VehiclePhotosContract.Effect.NavigateBack)
        }
    }

    private fun loadVehicle() {
        viewModelScope.launch {
            when (val result = vehiclesRepository.getVehicle(vehicleId)) {
                is AuthResult.Success -> {
                    val vehicle = result.data
                    _state.update { it.copy(brand = vehicle.brand, model = vehicle.model, plate = vehicle.plate) }
                }
                is AuthResult.Error -> sendEffect(VehiclePhotosContract.Effect.ShowError(result.message))
            }
        }
    }

    private fun loadExistingPhotos() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = rentalsRepository.getPhotos(rentalId)) {
                is AuthResult.Success -> applyPhotosState(result.data)
                is AuthResult.Error   -> _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handlePhotoCaptured(side: VehiclePhotosContract.PhotoSide, uri: Uri) {
        _state.update { it.copyWithUri(side, uri).copy(uploadingSide = side) }
        viewModelScope.launch {
            when (val result = rentalsRepository.uploadPhoto(rentalId, side.name, uri)) {
                is AuthResult.Success -> {
                    _state.update { it.copy(uploadingSide = null, uploadedCount = result.data.uploadedCount) }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(uploadingSide = null) }
                    sendEffect(VehiclePhotosContract.Effect.ShowError(result.message))
                }
            }
        }
    }

    private fun handleStartRentalClicked() {
        if (!_state.value.canStart) return
        _state.update { it.copy(isStarting = true) }
        viewModelScope.launch {
            when (val result = rentalsRepository.startRental(rentalId)) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isStarting = false) }
                    sendEffect(VehiclePhotosContract.Effect.NavigateToActiveRental(rentalId))
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isStarting = false) }
                    sendEffect(VehiclePhotosContract.Effect.ShowError(result.message))
                }
            }
        }
    }

    private fun applyPhotosState(data: RentalPhotosStateDto) {
        _state.update { current ->
            var next = current.copy(isLoading = false, uploadedCount = data.uploadedCount)
            data.photos.forEach { photo ->
                val side = runCatching { VehiclePhotosContract.PhotoSide.valueOf(photo.side) }.getOrNull()
                if (side != null) next = next.copyWithUri(side, Uri.parse(photo.imageUrl))
            }
            next
        }
    }

    private fun sendEffect(effect: VehiclePhotosContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
