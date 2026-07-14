package com.turkcell.rencar_pair.data.network.dto

data class RegisterDto(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val referralCode: String? = null
)

data class LoginDto(
    val phone: String
)

data class VerifyOtpDto(
    val phone: String,
    val code: String
)

data class RefreshTokenDto(
    val refreshToken: String
)

data class UserResponseDto(
    val id: String,
    val email: String,
    val phone: String?,
    val fullName: String,
    val role: String,
    val referralCode: String?,
    val createdAt: String,
    val updatedAt: String
)

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponseDto
)

data class OtpRequiredResponseDto(
    val message: String,
    val phone: String,
    val expiresAt: String
)

data class MessageResponseDto(
    val message: String
)
