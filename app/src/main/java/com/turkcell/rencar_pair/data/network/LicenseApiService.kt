package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.LicenseResponseDto
import com.turkcell.rencar_pair.data.network.dto.LicenseStatusResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LicenseApiService {

    @Multipart
    @POST("license/upload")
    suspend fun upload(
        @Part front: MultipartBody.Part,
        @Part back: MultipartBody.Part,
        @Part selfie: MultipartBody.Part
    ): Response<LicenseResponseDto>

    @GET("license/status")
    suspend fun status(): Response<LicenseStatusResponseDto>
}
