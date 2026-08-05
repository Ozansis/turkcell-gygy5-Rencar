package com.turkcell.rencar_pair.data.repository

import java.io.IOException
import retrofit2.Response

private const val CONNECTION_ERROR_MESSAGE = "Bağlantı hatası, lütfen tekrar deneyin."

/**
 * Repository katmanındaki tekrar eden try/catch + isSuccessful/body kalıbının tek merkezi hali.
 * Gövde döndüren uçlar için kullanılır; 204/boş gövdeli uçlar için [safeUnitCall]'a bakınız.
 */
internal suspend fun <T> safeCall(call: suspend () -> Response<T>): AuthResult<T> {
    return try {
        val response = call()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            AuthResult.Success(body)
        } else {
            AuthResult.Error(response.code(), response.extractErrorMessage())
        }
    } catch (e: IOException) {
        AuthResult.Error(code = null, message = CONNECTION_ERROR_MESSAGE)
    }
}

/** [safeCall] ile aynı, ancak 204/boş gövde döndüren uçlar için body kontrolü yapmaz. */
internal suspend fun safeUnitCall(call: suspend () -> Response<Unit>): AuthResult<Unit> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            AuthResult.Success(Unit)
        } else {
            AuthResult.Error(response.code(), response.extractErrorMessage())
        }
    } catch (e: IOException) {
        AuthResult.Error(code = null, message = CONNECTION_ERROR_MESSAGE)
    }
}
