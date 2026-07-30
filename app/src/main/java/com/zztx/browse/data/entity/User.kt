package com.zztx.browse.data.entity

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatar: String?,
    val token: String,
    val refreshToken: String,
    val expiresAt: Long
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)