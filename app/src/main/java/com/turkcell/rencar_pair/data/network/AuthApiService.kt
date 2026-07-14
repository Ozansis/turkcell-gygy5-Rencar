package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.network.dto.AuthResponseDto
import com.turkcell.rencar_pair.data.network.dto.LoginDto
import com.turkcell.rencar_pair.data.network.dto.MessageResponseDto
import com.turkcell.rencar_pair.data.network.dto.OtpRequiredResponseDto
import com.turkcell.rencar_pair.data.network.dto.RefreshTokenDto
import com.turkcell.rencar_pair.data.network.dto.RegisterDto
import com.turkcell.rencar_pair.data.network.dto.UserResponseDto
import com.turkcell.rencar_pair.data.network.dto.VerifyOtpDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterDto): Response<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(@Body body: LoginDto): Response<OtpRequiredResponseDto>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpDto): Response<AuthResponseDto>

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenDto): Response<AuthResponseDto>

    @POST("auth/logout")
    suspend fun logout(): Response<MessageResponseDto>

    @GET("auth/me")
    suspend fun me(): Response<UserResponseDto>
}
