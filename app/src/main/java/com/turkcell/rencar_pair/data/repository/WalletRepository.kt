package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.WalletApiService
import com.turkcell.rencar_pair.data.network.dto.TopupDto
import com.turkcell.rencar_pair.data.network.dto.WalletResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletApiService: WalletApiService
) {

    suspend fun getWallet(): AuthResult<WalletResponseDto> =
        safeCall { walletApiService.getWallet() }

    suspend fun topup(amount: Double): AuthResult<WalletResponseDto> =
        safeCall { walletApiService.topup(TopupDto(amount)) }
}
