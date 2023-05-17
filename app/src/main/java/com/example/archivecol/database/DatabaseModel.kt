package com.example.archivecol.database

data class Category(
    var id: Int = 0,
    var name: String = "",
    val goal: Int = 0
)

data class Item(
    var id: Int = 0,
    var categoryId: Int = 0,
    var name: String = "",
    var description: String = "",
    var count: Int? = 0,
    var photoPath: String? = null,
    var isCollected: Boolean = false
)