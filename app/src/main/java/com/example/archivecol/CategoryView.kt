package com.example.archivecol

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CategoryView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_view)

        val categoryName = intent.getStringExtra("categoryName")
        val categoryId = intent.getIntExtra("categoryId", -1)

        // use the categoryName to display the details of the category

        val title = findViewById<TextView>(R.id.categoryTitle)
        title.text = categoryName
    }
}
