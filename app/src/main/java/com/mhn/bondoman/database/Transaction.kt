package com.mhn.bondoman.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val transaction_id: Int?,
    @ColumnInfo(name = "transaction_owner") val transaction_owner: String,
    @ColumnInfo(name = "transaction_name") val transaction_name: String,
    @ColumnInfo(name = "transaction_date") val transaction_date: String,
    @ColumnInfo(name = "transaction_price") val transaction_price: Int,
    @ColumnInfo(name = "transaction_category") val transaction_category: String,
    @ColumnInfo(name = "transaction_location") val transaction_location: String,
//    @ColumnInfo(name = "transaction_latitude") val transaction_latitude: Double,
//    @ColumnInfo(name = "transaction_longitude") val transaction_longitude: Double
)

data class TransactionSummary(
    val transaction_date: String,
    val total_price: Float
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE transaction_owner = :email ORDER BY transaction_date DESC")
    suspend fun index(email: String): List<Transaction>

    @Insert
    suspend fun store(vararg transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE transaction_id = :transactionId")
    suspend fun getTransactionById(transactionId: Int): Transaction?

    @Delete
    suspend fun delete(vararg transaction: Transaction)

    @Query("SELECT transaction_date, SUM(transaction_price) AS total_price FROM transactions WHERE transaction_owner = :email AND transaction_category = :category GROUP BY transaction_date ORDER BY transaction_date DESC LIMIT 7")
    suspend fun getLast7Transaction(email: String, category: String): List<TransactionSummary>
}