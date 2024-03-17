package com.mhn.bondoman.models

data class Item(
    val name: String,
    val qty: Int,
    val price: Double
)

data class Items(
    val items: List<Item>
)
data class ItemsResponse(
    val items: Items
)