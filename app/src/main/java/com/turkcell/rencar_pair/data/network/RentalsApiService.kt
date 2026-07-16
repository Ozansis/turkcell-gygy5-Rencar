package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.ActiveRentalResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateRentalDto
import com.turkcell.rencar_pair.data.network.dto.RentalResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RentalsApiService {

    @POST("rentals")
    suspend fun createRental(@Body body: CreateRentalDto): Response<RentalResponseDto>

    @GET("rentals/active")
    suspend fun getActiveRental(): Response<ActiveRentalResponseDto>

    @POST("rentals/{id}/finish")
    suspend fun finishRental(@Path("id") id: String): Response<RentalResponseDto>
}
