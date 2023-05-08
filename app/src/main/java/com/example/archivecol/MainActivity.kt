package com.example.archivecol

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addCategory = findViewById<Button>(R.id.addCategoryButton)

        addCategory.setOnClickListener {
            fun createCategory(categoryName: String, goalNumber: Int) {
                val categoriesRef = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl("gs://archivecol.appspot.com")
                val categoryId = categoriesRef.push().key
                if (categoryId != null) {
                    val category = hashMapOf("name" to categoryName, "goal" to goalNumber)
                    categoriesRef.child(categoryId).setValue(category)
                }
            }

            class CategoryInputDialog(context: Context) {

                private val dialogView = LayoutInflater.from(context).inflate(
                    R.layout.category_input_dialog, null
                )
                private val categoryInput = dialogView.findViewById<EditText>(R.id.category_input)
                private val goalInput = dialogView.findViewById<EditText>(R.id.goal_input)

                private val dialog = AlertDialog.Builder(context)
                    .setTitle("Create category")
                    .setView(dialogView)
                    .setPositiveButton("Create") { _, _ ->
                        val categoryName = categoryInput.text.toString()
                        val goalNumber = goalInput.text.toString().toIntOrNull() ?: 0
                        if (categoryName.isNotEmpty()) {
                            createCategory(categoryName, goalNumber)
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .create()

                private fun createCategory(categoryName: String, goalNumber: Int) {
                    val categoriesRef = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("gs://archivecol.appspot.com")
                    val categoryId = categoriesRef.push().key
                    if (categoryId != null) {
                        val category = hashMapOf("name" to categoryName, "goal" to goalNumber)
                        categoriesRef.child(categoryId).setValue(category)
                    }
                }
            }

        }
    }
}
