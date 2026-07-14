package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private val NO_AUTH_PATH_SUFFIXES = listOf(
    "auth/register",
    "auth/login",
    "auth/verify-otp",
    "auth/refresh"
)

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore
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

        return chain.proceed(authorizedRequest)
    }
}
