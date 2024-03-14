package com.example.bondoman.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val transaction_id: Int?,
    @ColumnInfo(name="transaction_owner") val transaction_owner: String,
    @ColumnInfo(name="transaction_name") val transaction_name: String,
    @ColumnInfo(name="transaction_price") val transaction_price: Int,
    @ColumnInfo(name="transaction_category") val transaction_category: String,
    @ColumnInfo(name="transaction_location") val transaction_location: String
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE transaction_owner = :email")
    fun index(email: String): List<Transaction>

    @Insert
    fun store(vararg transaction: Transaction)
}