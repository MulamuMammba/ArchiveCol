package com.example.archivecol.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archivecol.R
import com.example.archivecol.database.adapters.CategoryAdapter
import com.example.archivecol.database.firebase.FirebaseSync
import com.example.archivecol.database.sqlite.DatabaseHelper
import com.example.archivecol.model.Category

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var categoryList: MutableList<Category> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val achievement: ImageButton = findViewById(R.id.achievementButton)

        achievement.setOnClickListener {
            startActivity(Intent(this, AchievementsScreen::class.java))
        }

        // Get categories from the database
        val dbHelper = DatabaseHelper(this)
        categoryList = dbHelper.getAllCategories() as MutableList<Category>

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter =
            CategoryAdapter(categoryList) { category -> onCategoryItemClick(category as Category) }
        recyclerView.adapter = adapter

    }

    private fun refreshView() {
        val adapter =
            CategoryAdapter(categoryList) { category -> onCategoryItemClick(category as Category) }
        recyclerView.adapter = adapter
    }

    fun addCategory(view: View) {
        val dialog = AlertDialog.Builder(view.context)
        val inflater = LayoutInflater.from(view.context)
        val dialogView = inflater.inflate(R.layout.category_input_dialog, null)
        dialog.setView(dialogView)

        val categoryName = dialogView.findViewById<EditText>(R.id.category_input)
        val categoryGoal = dialogView.findViewById<EditText>(R.id.goal_input)
        val cancelButton: Button = dialogView.findViewById(R.id.cancel_button)
        val addButton: Button = dialogView.findViewById(R.id.add_button)

        val alertDialog = dialog.create()

        addButton.setOnClickListener {
            var goalNum: Int = 1
            val name = categoryName.text.toString().trim()
            val goal = categoryGoal.text.toString().trim()

            if (name.isNotEmpty() && goal.isNotEmpty()) {
                val dbHelper = DatabaseHelper(this)
                val category = Category(categoryList.size + 1, name, goalNum)
                categoryList.add(category)
                dbHelper.addCategory(category)
                FirebaseSync.addCategory(category)
                refreshView()
                Toast.makeText(
                    view.context,
                    "Successfully added ${category.name}",
                    Toast.LENGTH_SHORT
                ).show()
                alertDialog.dismiss()
            } else {
                Toast.makeText(view.context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }

            try {
                goalNum = goal.toInt()
            } catch (e: NumberFormatException) {
                // Handle the case when the input is not an integer
                Toast.makeText(
                    view.context,
                    "$goal is not a number",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        cancelButton.setOnClickListener {
            Toast.makeText(view.context, "Sad you didn't add anything", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun onCategoryItemClick(category: Category) {

        val intent = Intent(this, CategoryView::class.java)

        // Pass list of items, category id, and category name to CategoryView activity
        intent.putExtra("category_id", category.id)
        intent.putExtra("category_name", category.name)

        startActivity(intent)
        this.finish()
    }

    override fun onBackPressed() {
        // Do nothing to prevent going back
    }
}