package com.example.bondoman.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object TransactionContract {
    object Transaction : BaseColumns {
        const val TABLE_NAME = "transactions"
        const val COLUMN_ID = "transaction_id"
        const val COLUMN_OWNER = "transaction_owner"
        const val COLUMN_NAME = "transaction_name"
        const val COLUMN_PRICE = "transaction_price"
        const val COLUMN_CATEGORY = "transaction_category"
        const val COLUMN_LOCATION = "transaction_location"
    }
}

private const val CREATE_TABLE_TRANSACTIONS =
            "CREATE TABLE ${TransactionContract.Transaction.TABLE_NAME} (" +
                "${TransactionContract.Transaction.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "${TransactionContract.Transaction.COLUMN_OWNER} TEXT," +
                "${TransactionContract.Transaction.COLUMN_NAME} TEXT," +
                "${TransactionContract.Transaction.COLUMN_PRICE} INTEGER," +
                "${TransactionContract.Transaction.COLUMN_CATEGORY} TEXT," +
                "${TransactionContract.Transaction.COLUMN_LOCATION} TEXT)"


class DatabaseHelper(context: Context): SQLiteOpenHelper(context,  DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "bondoman.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TRANSACTIONS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${TransactionContract.Transaction.TABLE_NAME}")
        onCreate(db)
    }

}