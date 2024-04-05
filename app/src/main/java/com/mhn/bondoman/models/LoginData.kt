package com.mhn.bondoman.models

data class LoginBody(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: Int,
    val token: String
)

data class TokenResponse(
    val nim: Int,
    val iat: Long,
    val exp: Long
)