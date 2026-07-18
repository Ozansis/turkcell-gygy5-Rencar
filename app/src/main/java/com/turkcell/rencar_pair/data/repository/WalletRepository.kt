package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.network.WalletApiService
import com.turkcell.rencar_pair.data.network.dto.TopupDto
import com.turkcell.rencar_pair.data.network.dto.WalletResponseDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletApiService: WalletApiService
) {

    suspend fun getWallet(): AuthResult<WalletResponseDto> {
        return try {
            val response = walletApiService.getWallet()
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

    suspend fun topup(amount: Double): AuthResult<WalletResponseDto> {
        return try {
            val response = walletApiService.topup(TopupDto(amount))
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
