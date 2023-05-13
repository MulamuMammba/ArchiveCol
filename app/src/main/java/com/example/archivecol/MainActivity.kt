package com.example.archivecol

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archivecol.database.Category
import com.example.archivecol.database.CategoryAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryList: MutableList<Category>
    lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the ObjectBox database


        // Load categories from the database


        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter =
            CategoryAdapter(categoryList) { category -> onCategoryItemClick(category as Category) }
        recyclerView.adapter = adapter
    }

    private fun onCategoryItemClick(category: Category) {
        val intent = Intent(this@MainActivity, CategoryView::class.java)
        intent.putExtra("category_id", category.id)
        startActivity(intent)
    }
}
