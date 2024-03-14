package com.example.bondoman.api

data class Transaction(
    val transaction_id: Int,
    val transaction_name: String,
    val transaction_price: Int,
    val transaction_category: String,
    val transaction_location: String
)