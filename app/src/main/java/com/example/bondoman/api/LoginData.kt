package com.example.bondoman.api

data class LoginBody(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: Int,
    val token: String
)