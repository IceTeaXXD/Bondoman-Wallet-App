package com.mhn.bondoman.api

import retrofit2.Retrofit

class RetrofitClient {
    companion object {
        @Volatile
        private var retrofit: Retrofit? = null

        fun getInstance(): retrofit2.Retrofit {
            if (retrofit == null) {
                retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl("https://pbd-backend-2024.vercel.app/")
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }
}