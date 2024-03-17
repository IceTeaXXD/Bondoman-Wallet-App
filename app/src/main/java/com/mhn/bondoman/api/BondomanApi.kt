package com.mhn.bondoman.api

import com.mhn.bondoman.models.ItemsResponse
import com.mhn.bondoman.models.LoginBody
import com.mhn.bondoman.models.LoginResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BondomanApi {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginBody: LoginBody
    ): LoginResponse

    @Multipart
    @POST("api/bill/upload")
    suspend fun uploadBill(
        @Header("Authorization") userToken: String,
        @Part file: MultipartBody.Part
    ): ItemsResponse

    companion object {
        @Volatile
        private var ins: BondomanApi? = null
        fun getInstance(): BondomanApi {
            if (ins == null) {
                ins = RetrofitClient.getInstance().create(BondomanApi::class.java)
            }
            return ins!!
        }
    }
}