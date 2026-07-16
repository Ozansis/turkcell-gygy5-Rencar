package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.ActiveRentalResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateRentalDto
import com.turkcell.rencar_pair.data.network.dto.RentalPhotosStateDto
import com.turkcell.rencar_pair.data.network.dto.RentalResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface RentalsApiService {

    @POST("rentals")
    suspend fun createRental(@Body body: CreateRentalDto): Response<RentalResponseDto>

    @GET("rentals/active")
    suspend fun getActiveRental(): Response<ActiveRentalResponseDto>

    @POST("rentals/{id}/finish")
    suspend fun finishRental(@Path("id") id: String): Response<RentalResponseDto>

    @Multipart
    @POST("rentals/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") id: String,
        @Part("side") side: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<RentalPhotosStateDto>

    @GET("rentals/{id}/photos")
    suspend fun getPhotos(@Path("id") id: String): Response<RentalPhotosStateDto>

    @POST("rentals/{id}/start")
    suspend fun startRental(@Path("id") id: String): Response<RentalResponseDto>
}
