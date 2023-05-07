package com.example.archivecol

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ItemInputDialog(context: Context) {

    private val dialogView = LayoutInflater.from(context).inflate(
        R.layout.item_input_dialog, null
    )
    private val categoryInput = dialogView.findViewById<EditText>(R.id.category_input)
    private val descriptionInput = dialogView.findViewById<EditText>(R.id.description_input)
    private val dateInput = dialogView.findViewById<EditText>(R.id.date_input)
    private val photoButton = dialogView.findViewById<Button>(R.id.photo_button)

    private val dialog = AlertDialog.Builder(context)
        .setTitle("Add item")
        .setView(dialogView)
        .setPositiveButton("Add") { _, _ ->
            val categoryName = categoryInput.text.toString()
            val description = descriptionInput.text.toString()
            val acquisitionDate = dateInput.text.toString()
            if (categoryName.isNotEmpty() && description.isNotEmpty() && acquisitionDate.isNotEmpty()) {
                addItem(categoryName, description, acquisitionDate)
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton("Cancel", null)
        .create()

    private lateinit var photoUri: Uri

    init {
        photoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            val activity = context as Activity
            activity.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    fun show() {
        dialog.show()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            photoUri = data.data ?: return
        }
    }

    private fun addItem(categoryName: String, description: String, acquisitionDate: String) {
        val itemsRef = FirebaseDatabase.getInstance().getReference("items")
        val newItemRef = itemsRef.push()
        val item = Item(categoryName, description, acquisitionDate, newItemRef.key!!)
        newItemRef.setValue(item).addOnSuccessListener {
            Toast.makeText(dialogView.context, "Item added successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(dialogView.context, "Failed to add item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadPhoto(itemId: String) {
        val storageRef = FirebaseStorage.getInstance().getReference("items/$itemId")
        val photoRef = storageRef.child(photoUri.lastPathSegment ?: "item-photo")
        photoRef.putFile(photoUri).addOnSuccessListener {
            Toast.makeText(dialogView.context, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(dialogView.context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
    }

}
