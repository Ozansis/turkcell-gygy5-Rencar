package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.VehiclesApiService
import com.turkcell.rencar_pair.data.network.dto.QuoteResponseDto
import com.turkcell.rencar_pair.data.network.dto.VehicleResponseDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehiclesRepository @Inject constructor(
    private val vehiclesApiService: VehiclesApiService
) {

    suspend fun listVehicles(includeBusy: Boolean = true): AuthResult<List<VehicleResponseDto>> {
        return try {
            val response = vehiclesApiService.listVehicles(includeBusy = includeBusy.toString())
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

    suspend fun getVehicle(id: String): AuthResult<VehicleResponseDto> {
        return try {
            val response = vehiclesApiService.getVehicle(id)
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

    suspend fun getQuote(id: String, plan: String, minutes: Int): AuthResult<QuoteResponseDto> {
        return try {
            val response = vehiclesApiService.getQuote(id, plan, minutes)
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
