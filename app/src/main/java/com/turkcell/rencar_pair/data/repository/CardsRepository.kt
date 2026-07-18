package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.CardsApiService
import com.turkcell.rencar_pair.data.network.dto.CardResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateCardDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardsRepository @Inject constructor(
    private val cardsApiService: CardsApiService
) {

    suspend fun listCards(): AuthResult<List<CardResponseDto>> {
        return try {
            val response = cardsApiService.listCards()
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

    suspend fun addCard(brand: String, last4: String, expMonth: Int, expYear: Int): AuthResult<CardResponseDto> {
        return try {
            val response = cardsApiService.addCard(CreateCardDto(brand, last4, expMonth, expYear))
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

    suspend fun setDefaultCard(id: String): AuthResult<CardResponseDto> {
        return try {
            val response = cardsApiService.setDefaultCard(id)
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

    suspend fun deleteCard(id: String): AuthResult<Unit> {
        return try {
            val response = cardsApiService.deleteCard(id)
            if (response.isSuccessful) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(response.code(), response.extractErrorMessage())
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }
}
