package com.example.bondoman.database

import android.content.ContentValues
import android.content.Context
import com.example.bondoman.api.Transaction

class DatabaseController(context: Context) {
    private val databaseHelper = DatabaseHelper(context)

    fun store(email: String, transaction_name: String, transaction_price: Int, transaction_category: String, transaction_location: String) {
        val db = databaseHelper.writableDatabase

        val values = ContentValues().apply {
            put(TransactionContract.Transaction.COLUMN_OWNER, email)
            put(TransactionContract.Transaction.COLUMN_NAME, transaction_name)
            put(TransactionContract.Transaction.COLUMN_PRICE, transaction_price)
            put(TransactionContract.Transaction.COLUMN_CATEGORY, transaction_category)
            put(TransactionContract.Transaction.COLUMN_LOCATION, transaction_location)
        }

        db.insert(TransactionContract.Transaction.TABLE_NAME, null, values)
        db.close()
    }

    fun index(email:String): List<Transaction>{
        val list = mutableListOf<Transaction>()

        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${TransactionContract.Transaction.TABLE_NAME} WHERE transaction_owner = ?", arrayOf(email))

        while(cursor.moveToNext()){
            val transaction_id = cursor.getInt(cursor.getColumnIndexOrThrow("transaction_id"))
            val transaction_name = cursor.getString(cursor.getColumnIndexOrThrow("transaction_name"))
            val transaction_price = cursor.getInt(cursor.getColumnIndexOrThrow("transaction_price"))
            val transaction_category = cursor.getString(cursor.getColumnIndexOrThrow("transaction_category"))
            val transaction_location = cursor.getString(cursor.getColumnIndexOrThrow("transaction_location"))

            list.add(Transaction(transaction_id, transaction_name, transaction_price, transaction_category, transaction_location))
        }

        cursor.close()
        db.close()

        return list
    }
}