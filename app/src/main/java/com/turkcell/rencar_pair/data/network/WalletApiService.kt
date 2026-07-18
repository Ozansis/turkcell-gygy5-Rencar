package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.TopupDto
import com.turkcell.rencar_pair.data.network.dto.WalletResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApiService {

    @GET("wallet")
    suspend fun getWallet(): Response<WalletResponseDto>

    @POST("wallet/topup")
    suspend fun topup(@Body body: TopupDto): Response<WalletResponseDto>
}
