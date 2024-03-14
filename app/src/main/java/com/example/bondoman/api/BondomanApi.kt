package com.example.bondoman.api

import com.example.bondoman.models.LoginBody
import com.example.bondoman.models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BondomanApi {
    @POST("api/auth/login")
    suspend fun login(@Body loginBody: LoginBody): LoginResponse
    companion object {
        private val ins = RetrofitClient.getInstance().create(BondomanApi::class.java)
        fun getInstance(): BondomanApi { return ins }
    }
}