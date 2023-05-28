package com.example.archivecol.database.firebase

import android.content.Context
import com.example.archivecol.database.sqlite.DatabaseHelper
import com.example.archivecol.model.Category
import com.example.archivecol.model.Item
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseSync {
    companion object {

        fun addItem(item: Item) {

            val databaseReference = FirebaseDatabase.getInstance().reference
            val itemRef = databaseReference.child("items").child(item.id.toString())
            itemRef.setValue(item)

        }

        fun addCategory(category: Category) {

            val databaseReference = FirebaseDatabase.getInstance().reference
            val categoryRef = databaseReference.child("categories").child(category.id.toString())
            categoryRef.setValue(category)

        }

        fun deleteItem(item: Item) {
            val itemRef = FirebaseDatabase.getInstance().getReference("items")
            itemRef.child(item.id.toString()).removeValue()
        }

        fun deleteCategory(context: Context, categoryId: Int) {
            val db = DatabaseHelper(context)
            val databaseReference = FirebaseDatabase.getInstance().reference

            // Retrieve items associated with the category from Firebase
            val itemsRef = databaseReference.child("items")
            val itemsQuery = itemsRef.orderByChild("categoryId").equalTo(categoryId.toDouble())
            itemsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (itemSnapshot in dataSnapshot.children) {
                        val itemId = itemSnapshot.key
                        itemId?.let {
                            // Delete the item from Firebase
                            itemsRef.child(itemId).removeValue()
                        }
                    }

                    // Delete the category from Firebase
                    databaseReference.child("categories").child(categoryId.toString()).removeValue()

                    // Delete the category locally
                    db.deleteCategory(categoryId)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })
        }


        fun refreshDatabase(dbHelper: DatabaseHelper) {
            val firebaseRef = FirebaseDatabase.getInstance().reference

            // Clear existing data from local database
            dbHelper.clearDatabase()

            // Retrieve categories from Firebase and save to local database
            val categoriesRef = firebaseRef.child("categories")
            categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (categorySnapshot in dataSnapshot.children) {
                        val category = categorySnapshot.getValue(Category::class.java)
                        if (category != null) {
                            dbHelper.addCategory(category)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })

            val itemsRef = firebaseRef.child("items")
            itemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (itemSnapshot in dataSnapshot.children) {
                        val item = itemSnapshot.getValue(Item::class.java)
                        if (item != null) {
                            dbHelper.addItem(item)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled if needed
                }
            })
        }

    }

}