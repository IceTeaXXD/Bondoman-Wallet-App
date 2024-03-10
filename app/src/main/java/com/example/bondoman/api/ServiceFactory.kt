package com.example.bondoman.api

import retrofit2.Call

class ServiceFactory constructor(private val service: BackendService) {
    suspend fun login(loginBody: LoginBody): LoginResponse {
        return service.login(loginBody)
    }
}