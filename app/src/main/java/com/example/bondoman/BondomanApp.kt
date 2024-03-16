package com.example.bondoman

import android.app.Application
import com.example.bondoman.database.AppDatabase
import com.example.bondoman.repository.TransactionRepository

class BondomanApp : Application(){
    private lateinit var transactionRepository: TransactionRepository

    override fun onCreate(){
        super.onCreate()
        transactionRepository = TransactionRepository(
            AppDatabase.getInstance(applicationContext)
        )
    }
    fun getRepository(): TransactionRepository {
        return transactionRepository
    }
}