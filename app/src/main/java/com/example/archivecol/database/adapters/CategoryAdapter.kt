package com.example.archivecol.database.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.archivecol.R
import com.example.archivecol.model.Category
import com.example.archivecol.ui.CategoryView

class CategoryAdapter(private val categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)
        val categoryGoal: TextView = view.findViewById(R.id.categoryGoal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.name
        holder.categoryGoal.text = "Goal: ${category.goal}"

        // Set onClick listener for the current category item
        holder.itemView.setOnClickListener {
            onCategoryItemClick(
                holder.itemView.context,
                category
            )
        }
    }

    private fun onCategoryItemClick(context: Context, category: Category) {
        val intent = Intent(context, CategoryView::class.java)

        // Pass list of items, category id and category name to CategoryView activity
        intent.putExtra("category_id", category.id)
        intent.putExtra("category_name", category.name)

        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}