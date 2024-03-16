package com.example.bondoman

import android.app.Application
import com.example.bondoman.database.AppDatabase
import com.example.bondoman.repository.AppRepository

class BondomanApp : Application(){
    private lateinit var appRepository: AppRepository

    override fun onCreate(){
        super.onCreate()
        appRepository = AppRepository(
            AppDatabase.getInstance(applicationContext)
        )
    }
    fun getRepository(): AppRepository {
        return appRepository
    }
}