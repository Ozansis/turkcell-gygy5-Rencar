package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.CheckoutFormInitializeResponseDto
import com.turkcell.rencar_pair.data.network.dto.InitializeCheckoutFormDto
import com.turkcell.rencar_pair.data.network.dto.IyzicoPaymentResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IyzicoApiService {

    @POST("iyzico/checkout-form/initialize")
    suspend fun initializeCheckoutForm(
        @Body body: InitializeCheckoutFormDto
    ): Response<CheckoutFormInitializeResponseDto>

    @GET("iyzico/checkout-form/result/{token}")
    suspend fun getCheckoutFormResult(
        @Path("token") token: String
    ): Response<IyzicoPaymentResponseDto>
}
