package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.CardsApiService
import com.turkcell.rencar_pair.data.network.dto.CardResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateCardDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardsRepository @Inject constructor(
    private val cardsApiService: CardsApiService
) {

    suspend fun listCards(): AuthResult<List<CardResponseDto>> =
        safeCall { cardsApiService.listCards() }

    suspend fun addCard(brand: String, last4: String, expMonth: Int, expYear: Int): AuthResult<CardResponseDto> =
        safeCall { cardsApiService.addCard(CreateCardDto(brand, last4, expMonth, expYear)) }

    suspend fun setDefaultCard(id: String): AuthResult<CardResponseDto> =
        safeCall { cardsApiService.setDefaultCard(id) }

    suspend fun deleteCard(id: String): AuthResult<Unit> =
        safeUnitCall { cardsApiService.deleteCard(id) }
}
