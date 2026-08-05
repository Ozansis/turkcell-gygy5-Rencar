package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.VehiclesApiService
import com.turkcell.rencar_pair.data.network.dto.QuoteResponseDto
import com.turkcell.rencar_pair.data.network.dto.VehicleResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehiclesRepository @Inject constructor(
    private val vehiclesApiService: VehiclesApiService
) {

    suspend fun listVehicles(includeBusy: Boolean = true): AuthResult<List<VehicleResponseDto>> =
        safeCall { vehiclesApiService.listVehicles(includeBusy = includeBusy.toString()) }

    suspend fun getVehicle(id: String): AuthResult<VehicleResponseDto> =
        safeCall { vehiclesApiService.getVehicle(id) }

    suspend fun getQuote(id: String, plan: String, minutes: Int): AuthResult<QuoteResponseDto> =
        safeCall { vehiclesApiService.getQuote(id, plan, minutes) }
}
