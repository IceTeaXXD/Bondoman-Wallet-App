package com.example.bondoman.repository

import com.example.bondoman.database.AppDatabase
import com.example.bondoman.database.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class AppRepository(private val appDatabase: AppDatabase) {
    suspend fun getTransactionById(transactionId: Int?): Transaction?{
        return appDatabase.transactionDao().getTransactionById(transactionId!!)
    }
    suspend fun updateTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            appDatabase.transactionDao().update(transaction)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction){
        withContext(Dispatchers.IO){
            appDatabase.transactionDao().delete(transaction)
        }
    }
}