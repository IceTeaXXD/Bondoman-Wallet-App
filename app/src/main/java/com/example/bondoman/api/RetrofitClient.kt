package com.example.bondoman.api

class RetrofitClient {
    companion object {
        private val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

        fun getInstance(): retrofit2.Retrofit {
            return retrofit
        }
    }
}