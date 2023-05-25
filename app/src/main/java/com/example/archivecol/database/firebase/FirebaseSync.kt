package com.example.archivecol.database.firebase

import com.example.archivecol.database.Category
import com.example.archivecol.database.DatabaseHelper
import com.example.archivecol.database.Item
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

        fun deleteCategory(category: Category) {
            //Delete Category
            val categoryRef = FirebaseDatabase.getInstance().getReference("categories")
            categoryRef.child(category.id.toString()).removeValue()

            //Delete Its Children
            val itemRef = FirebaseDatabase.getInstance().getReference("items")
            itemRef.orderByChild("categoryId").equalTo(category.id.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (itemSnapshot in dataSnapshot.children) {
                            val itemId = itemSnapshot.key
                            itemSnapshot.ref.removeValue()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        //Empty
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