package com.example.archivecol.database

import io.objectbox.BoxStore
import io.objectbox.annotation.Entity

object ObjectBox {
    lateinit var store: BoxStore
        private set


}


@Entity
data class Category(val id: Int, val name: String, val goal: Int)

@Entity
data class Item(
    val id: Int,
    val categoryId: Int,
    val description: String,
    val dateAcquired: String,
    val photoPath: String
)