package com.example.bondoman.repository

import com.example.bondoman.database.AppDatabase
import com.example.bondoman.database.Transaction

class TransactionRepository(private val appDatabase: AppDatabase) {
    suspend fun getTransactionById(transactionId: Int?): Transaction?{
        return appDatabase.transactionDao().getTransactionById(transactionId!!)
    }
}