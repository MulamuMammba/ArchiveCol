package com.example.archivecol.database.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.archivecol.R
import com.example.archivecol.model.Item

class ItemAdapter(private val context: Context, private val items: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.item_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.comment)
        private val imageView: ImageView = itemView.findViewById(R.id.image)

        fun bind(item: Item) {
            nameTextView.text = item.name
            descriptionTextView.text = item.description

            if (!item.photoPath.isNullOrEmpty()) {
                // Load and display the image using the provided URL
                Glide.with(context)
                    .load(item.photoPath)
                    .into(imageView)
            } else {
                // Show a default image if the URL is not available
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }

}
