package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.ReservationsApiService
import com.turkcell.rencar_pair.data.network.dto.CreateReservationDto
import com.turkcell.rencar_pair.data.network.dto.ReservationResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationsRepository @Inject constructor(
    private val reservationsApiService: ReservationsApiService
) {

    suspend fun createReservation(vehicleId: String): AuthResult<ReservationResponseDto> =
        safeCall { reservationsApiService.createReservation(CreateReservationDto(vehicleId)) }
}
