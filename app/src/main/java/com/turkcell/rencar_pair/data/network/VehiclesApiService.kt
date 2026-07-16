package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.QuoteResponseDto
import com.turkcell.rencar_pair.data.network.dto.VehicleResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VehiclesApiService {

    @GET("vehicles")
    suspend fun listVehicles(
        @Query("type") type: String? = null,
        @Query("segment") segment: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("includeBusy") includeBusy: String? = null
    ): Response<List<VehicleResponseDto>>

    @GET("vehicles/{id}")
    suspend fun getVehicle(@Path("id") id: String): Response<VehicleResponseDto>

    @GET("vehicles/{id}/quote")
    suspend fun getQuote(
        @Path("id") id: String,
        @Query("plan") plan: String,
        @Query("minutes") minutes: Int
    ): Response<QuoteResponseDto>
}
