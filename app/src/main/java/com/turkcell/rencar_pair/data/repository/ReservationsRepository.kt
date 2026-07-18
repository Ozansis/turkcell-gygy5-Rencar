package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.ReservationsApiService
import com.turkcell.rencar_pair.data.network.dto.CreateReservationDto
import com.turkcell.rencar_pair.data.network.dto.ReservationResponseDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationsRepository @Inject constructor(
    private val reservationsApiService: ReservationsApiService
) {

    suspend fun createReservation(vehicleId: String): AuthResult<ReservationResponseDto> {
        return try {
            val response = reservationsApiService.createReservation(CreateReservationDto(vehicleId))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                AuthResult.Success(body)
            } else {
                AuthResult.Error(response.code(), response.extractErrorMessage())
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }
}
