package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.local.TokenStore
import com.turkcell.rencar_pair.data.network.AuthApiService
import com.turkcell.rencar_pair.data.network.dto.AuthResponseDto
import com.turkcell.rencar_pair.data.network.dto.LoginDto
import com.turkcell.rencar_pair.data.network.dto.OtpRequiredResponseDto
import com.turkcell.rencar_pair.data.network.dto.RefreshTokenDto
import com.turkcell.rencar_pair.data.network.dto.RegisterDto
import com.turkcell.rencar_pair.data.network.dto.UserResponseDto
import com.turkcell.rencar_pair.data.network.dto.VerifyOtpDto
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val code: Int?, val message: String) : AuthResult<Nothing>
}

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStore: TokenStore
) {

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        referralCode: String?
    ): AuthResult<Unit> {
        val result = safeCall {
            authApiService.register(RegisterDto(email, password, fullName, phone, referralCode))
        }
        if (result is AuthResult.Success) {
            tokenStore.saveTokens(result.data.accessToken, result.data.refreshToken)
        }
        return when (result) {
            is AuthResult.Success -> AuthResult.Success(Unit)
            is AuthResult.Error -> result
        }
    }

    suspend fun requestOtp(phone: String): AuthResult<OtpRequiredResponseDto> {
        return safeCall { authApiService.login(LoginDto(phone)) }
    }

    suspend fun verifyOtp(phone: String, code: String): AuthResult<Unit> {
        val result = safeCall { authApiService.verifyOtp(VerifyOtpDto(phone, code)) }
        if (result is AuthResult.Success) {
            tokenStore.saveTokens(result.data.accessToken, result.data.refreshToken)
        }
        return when (result) {
            is AuthResult.Success -> AuthResult.Success(Unit)
            is AuthResult.Error -> result
        }
    }

    suspend fun refresh(): AuthResult<AuthResponseDto> {
        val refreshToken = tokenStore.readRefreshToken()
            ?: return AuthResult.Error(code = null, message = "Kayıtlı bir oturum bulunamadı.")

        val result = safeCall { authApiService.refresh(RefreshTokenDto(refreshToken)) }
        if (result is AuthResult.Success) {
            tokenStore.saveTokens(result.data.accessToken, result.data.refreshToken)
        }
        return result
    }

    suspend fun logout() {
        try {
            authApiService.logout()
        } catch (e: IOException) {
            // Ağ hatası olsa bile kullanıcı yerel olarak çıkış yapabilmeli.
        } finally {
            tokenStore.clear()
        }
    }

    suspend fun getMe(): AuthResult<UserResponseDto> = safeCall { authApiService.me() }

    private suspend fun <T> safeCall(call: suspend () -> Response<T>): AuthResult<T> {
        return try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                AuthResult.Success(body)
            } else {
                AuthResult.Error(response.code(), "Sunucu hatası (kod: ${response.code()}).")
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }
}
