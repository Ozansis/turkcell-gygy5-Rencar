package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.CardResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateCardDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CardsApiService {

    @GET("cards")
    suspend fun listCards(): Response<List<CardResponseDto>>

    @POST("cards")
    suspend fun addCard(@Body body: CreateCardDto): Response<CardResponseDto>

    @PATCH("cards/{id}/default")
    suspend fun setDefaultCard(@Path("id") id: String): Response<CardResponseDto>

    @DELETE("cards/{id}")
    suspend fun deleteCard(@Path("id") id: String): Response<Unit>
}
