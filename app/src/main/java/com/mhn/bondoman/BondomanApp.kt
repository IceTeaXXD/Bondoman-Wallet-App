package com.mhn.bondoman

import android.app.Application
import com.mhn.bondoman.database.AppDatabase
import com.mhn.bondoman.repository.AppRepository

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