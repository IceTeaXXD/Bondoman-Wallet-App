package com.example.bondoman.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendService{
    @POST("/api/auth/login")
    suspend fun login(@Body loginBody: LoginBody): LoginResponse
}