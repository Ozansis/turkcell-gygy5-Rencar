package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.IyzicoApiService
import com.turkcell.rencar_pair.data.network.dto.CheckoutFormInitializeResponseDto
import com.turkcell.rencar_pair.data.network.dto.InitializeCheckoutFormDto
import com.turkcell.rencar_pair.data.network.dto.IyzicoPaymentResponseDto
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
    ): AuthResult<CheckoutFormInitializeResponseDto> =
        safeCall {
            iyzicoApiService.initializeCheckoutForm(
                InitializeCheckoutFormDto(
                    price = price,
                    description = description,
                    basketId = "rental-$rentalId"
                )
            )
        }

    suspend fun getCheckoutFormResult(token: String): AuthResult<IyzicoPaymentResponseDto> =
        safeCall { iyzicoApiService.getCheckoutFormResult(token) }
}
