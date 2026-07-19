package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.IyzicoApiService
import com.turkcell.rencar_pair.data.network.dto.CheckoutFormInitializeResponseDto
import com.turkcell.rencar_pair.data.network.dto.InitializeCheckoutFormDto
import com.turkcell.rencar_pair.data.network.dto.IyzicoPaymentResponseDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IyzicoRepository @Inject constructor(
    private val iyzicoApiService: IyzicoApiService
) {

    suspend fun initializeCheckoutForm(
        rentalId: String,
        price: Double,
        description: String? = null
    ): AuthResult<CheckoutFormInitializeResponseDto> {
        return try {
            val response = iyzicoApiService.initializeCheckoutForm(
                InitializeCheckoutFormDto(
                    price = price,
                    description = description,
                    basketId = "rental-$rentalId"
                )
            )
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

    suspend fun getCheckoutFormResult(token: String): AuthResult<IyzicoPaymentResponseDto> {
        return try {
            val response = iyzicoApiService.getCheckoutFormResult(token)
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
