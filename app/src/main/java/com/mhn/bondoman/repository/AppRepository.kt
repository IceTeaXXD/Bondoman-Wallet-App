package com.mhn.bondoman.repository

import com.mhn.bondoman.database.AppDatabase
import com.mhn.bondoman.database.Transaction
import com.mhn.bondoman.database.TransactionSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val appDatabase: AppDatabase) {
    suspend fun getTransactionById(transactionId: Int?): Transaction? {
        return appDatabase.transactionDao().getTransactionById(transactionId!!)
    }
    suspend fun getTransactionsByEmail(email: String): List<Transaction> {
        return appDatabase.transactionDao().index(email)
    }
    suspend fun updateTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            appDatabase.transactionDao().update(transaction)
        }
    }
    suspend fun getLast7Transaction(email: String, category: String) : List<TransactionSummary>{
        return appDatabase.transactionDao().getLast7Transaction(email,category)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            appDatabase.transactionDao().delete(transaction)
        }
    }
}