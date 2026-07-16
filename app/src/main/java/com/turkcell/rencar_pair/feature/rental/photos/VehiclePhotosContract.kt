package com.turkcell.rencar_pair.feature.rental.photos

import android.net.Uri

object VehiclePhotosContract {

    enum class PhotoSide { FRONT, BACK, LEFT, RIGHT }

    data class State(
        val rentalId: String = "",
        val vehicleId: String = "",
        val brand: String = "",
        val model: String = "",
        val plate: String = "",
        val frontUri: Uri? = null,
        val backUri: Uri? = null,
        val leftUri: Uri? = null,
        val rightUri: Uri? = null,
        val uploadingSide: PhotoSide? = null,
        val uploadedCount: Int = 0,
        val isStarting: Boolean = false,
        val isLoading: Boolean = false
    ) {
        val photosComplete: Boolean get() = uploadedCount >= 4
        val canStart: Boolean get() = photosComplete && !isStarting
        val progressLabel: String get() = "$uploadedCount / 4 çekildi"
        val startButtonLabel: String get() = if (photosComplete) "Kiralamayı Başlat" else "Kiralamayı Başlat · ${4 - uploadedCount} foto kaldı"

        fun uriFor(side: PhotoSide): Uri? = when (side) {
            PhotoSide.FRONT -> frontUri
            PhotoSide.BACK  -> backUri
            PhotoSide.LEFT  -> leftUri
            PhotoSide.RIGHT -> rightUri
        }

        fun copyWithUri(side: PhotoSide, uri: Uri): State = when (side) {
            PhotoSide.FRONT -> copy(frontUri = uri)
            PhotoSide.BACK  -> copy(backUri = uri)
            PhotoSide.LEFT  -> copy(leftUri = uri)
            PhotoSide.RIGHT -> copy(rightUri = uri)
        }
    }

    sealed interface Intent {
        data class CapturePhotoClicked(val side: PhotoSide)     : Intent
        data class PhotoCaptured(val side: PhotoSide, val uri: Uri) : Intent
        data object StartRentalClicked                          : Intent
        data object NavigateBack                                : Intent
    }

    sealed interface Effect {
        data class LaunchCamera(val side: PhotoSide)             : Effect
        data class NavigateToActiveRental(val rentalId: String)  : Effect
        data class ShowError(val message: String)                : Effect
        data object NavigateBack                                 : Effect
    }
}
