package com.turkcell.rencar_pair.data.repository

import com.turkcell.rencar_pair.data.local.CurrentUserSession
import com.turkcell.rencar_pair.data.local.TokenStore
import com.turkcell.rencar_pair.data.network.AuthApiService
import com.turkcell.rencar_pair.data.network.dto.AuthResponseDto
import com.turkcell.rencar_pair.data.network.dto.LoginDto
import com.turkcell.rencar_pair.data.network.dto.OtpRequiredResponseDto
import com.turkcell.rencar_pair.data.network.dto.RefreshTokenDto
import com.turkcell.rencar_pair.data.network.dto.RegisterDto
import com.turkcell.rencar_pair.data.network.dto.UserResponseDto
import com.turkcell.rencar_pair.data.network.dto.VerifyOtpDto
import com.google.gson.JsonParser
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val code: Int?, val message: String) : AuthResult<Nothing>
}

/**
 * Backend hata gövdesindeki ("statusCode"/"message"/"error") "message" alanını okur.
 * "message" NestJS validation hatalarında dizi olabilir; bu durumda satırlarla birleştirilir.
 * Ayrıştırma başarısız olursa jenerik "Sunucu hatası" metnine düşer.
 */
fun Response<*>.extractErrorMessage(): String {
    val fallback = "Sunucu hatası (kod: ${code()})."
    val raw = errorBody()?.string() ?: return fallback
    return try {
        val messageElement = JsonParser.parseString(raw).asJsonObject.get("message")
        when {
            messageElement == null || messageElement.isJsonNull -> fallback
            messageElement.isJsonArray -> messageElement.asJsonArray.joinToString("\n") { it.asString }
            else -> messageElement.asString
        }
    } catch (e: Exception) {
        fallback
    }
}

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStore: TokenStore,
    private val currentUserSession: CurrentUserSession
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
            currentUserSession.updateRole(result.data.user.role)
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
            currentUserSession.updateRole(result.data.user.role)
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
            currentUserSession.updateRole(result.data.user.role)
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
            currentUserSession.updateRole(null)
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
                AuthResult.Error(response.code(), response.extractErrorMessage())
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }
}
