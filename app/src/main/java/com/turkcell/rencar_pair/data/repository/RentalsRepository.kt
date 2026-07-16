package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.RentalsApiService
import com.turkcell.rencar_pair.data.network.dto.ActiveRentalResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateRentalDto
import com.turkcell.rencar_pair.data.network.dto.RentalResponseDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RentalsRepository @Inject constructor(
    private val rentalsApiService: RentalsApiService
) {

    suspend fun createRental(vehicleId: String, plan: String, endDate: String? = null): AuthResult<RentalResponseDto> {
        return try {
            val response = rentalsApiService.createRental(CreateRentalDto(vehicleId, plan, endDate))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                AuthResult.Success(body)
            } else {
                AuthResult.Error(response.code(), "Sunucu hatası (kod: ${response.code()}).")
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }

    suspend fun getActiveRental(): AuthResult<ActiveRentalResponseDto> {
        return try {
            val response = rentalsApiService.getActiveRental()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                AuthResult.Success(body)
            } else {
                AuthResult.Error(response.code(), "Sunucu hatası (kod: ${response.code()}).")
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }

    suspend fun finishRental(id: String): AuthResult<RentalResponseDto> {
        return try {
            val response = rentalsApiService.finishRental(id)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                AuthResult.Success(body)
            } else {
                AuthResult.Error(response.code(), "Sunucu hatası (kod: ${response.code()}).")
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }
}
