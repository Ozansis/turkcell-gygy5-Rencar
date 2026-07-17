package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.local.TokenStore
import com.turkcell.rencar_pair.data.repository.AuthRepository
import com.turkcell.rencar_pair.data.repository.AuthResult
import dagger.Lazy
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

private val NO_AUTH_PATH_SUFFIXES = listOf(
    "auth/register",
    "auth/login",
    "auth/verify-otp",
    "auth/refresh"
)

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val authRepositoryLazy: Lazy<AuthRepository>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val needsAuth = NO_AUTH_PATH_SUFFIXES.none { request.url.encodedPath.endsWith(it) }
        val accessToken = tokenStore.accessToken

        val authorizedRequest = if (needsAuth && accessToken != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            request
        }

        val response = chain.proceed(authorizedRequest)
        if (!needsAuth || response.code != 401) return response

        // Erişim token'ının süresi dolmuş olabilir: /auth/refresh ile yenileyip isteği bir kez tekrar dene.
        val newAccessToken = refreshAccessToken(staleToken = accessToken) ?: return response

        response.close()
        val retriedRequest = request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
        return chain.proceed(retriedRequest)
    }

    @Synchronized
    private fun refreshAccessToken(staleToken: String?): String? {
        val currentToken = tokenStore.accessToken
        if (currentToken != null && currentToken != staleToken) {
            // Bu istek beklerken başka bir istek zaten yenilemiş; onu kullan.
            return currentToken
        }
        val result = runBlocking { authRepositoryLazy.get().refresh() }
        return if (result is AuthResult.Success) tokenStore.accessToken else null
    }
}
