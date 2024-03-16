package com.example.bondoman.repository

import com.example.bondoman.database.AppDatabase
import com.example.bondoman.database.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(private val appDatabase: AppDatabase) {
    suspend fun getTransactionById(transactionId: Int?): Transaction?{
        return appDatabase.transactionDao().getTransactionById(transactionId!!)
    }
    suspend fun updateTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            appDatabase.transactionDao().update(transaction)
        }
    }
}