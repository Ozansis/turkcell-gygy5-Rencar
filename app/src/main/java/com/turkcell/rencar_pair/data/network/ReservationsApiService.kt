package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.CreateReservationDto
import com.turkcell.rencar_pair.data.network.dto.ReservationResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReservationsApiService {

    @POST("reservations")
    suspend fun createReservation(@Body body: CreateReservationDto): Response<ReservationResponseDto>
}
