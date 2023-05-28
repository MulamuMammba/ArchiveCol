package com.example.archivecol.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.archivecol.R
import com.example.archivecol.database.adapters.ItemAdapter
import com.example.archivecol.database.firebase.FirebaseSync
import com.example.archivecol.database.sqlite.DatabaseHelper
import com.example.archivecol.model.Achievements
import com.example.archivecol.model.Category
import com.example.archivecol.model.Item

class CategoryView : AppCompatActivity() {

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private var itemsList: MutableList<Item> = mutableListOf()
    private var dbHelper = DatabaseHelper(this)
    private var categoryId = 0
    private lateinit var categoryName: String
    private lateinit var imageUrl: String
    private var pictureAdded: Boolean = false

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Process the selected image URI here
            uri?.let {
                imageUrl = it.toString()
                pictureAdded = true
            }
        }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_view)

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.itemsView)

        // Retrieve category ID, name, and item list from intent
        categoryId = intent.getIntExtra("category_id", 0)
        categoryName = intent.getStringExtra("category_name").toString()

        // Set the title of the activity to the category name
        val title = findViewById<TextView>(R.id.categoryTitle)
        title.text = categoryName

        // Get the items for the category from the database
        itemsList = dbHelper.getItemsForCategory(categoryId) as MutableList<Item>

        // Display list of items
        itemAdapter = ItemAdapter(this, itemsList)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = itemAdapter

        //add on click listener
        val addItemsBtn = findViewById<Button>(R.id.addItems)
        addItemsBtn.setOnClickListener { view ->
            addItems(view)
        }
    }

    private fun refreshView() {
        itemAdapter = ItemAdapter(this, itemsList)
        recyclerView.adapter = itemAdapter
    }

    fun openEditDialog(view: View) {
        val dialog = AlertDialog.Builder(view.context)
        val inflater = LayoutInflater.from(view.context)
        val dialogView = inflater.inflate(R.layout.category_edit, null)
        dialog.setView(dialogView)
        val alertDialog = dialog.create()

        val newCategoryName = dialogView.findViewById<EditText>(R.id.new_category_name)
        val newCategoryGoal = dialogView.findViewById<EditText>(R.id.new_goal_number)
        val updateButton: Button = dialogView.findViewById(R.id.confirm_button)
        val deleteButton: Button = dialogView.findViewById(R.id.delete_button)

        val category = dbHelper.getCategoryById(categoryId)
        if (category != null) {
            newCategoryGoal.setText(category.goal.toString())
        }

        newCategoryName.setText(categoryName)

        //Delete the category and its item
        deleteButton.setOnClickListener {
            val categoryIdToDelete = categoryId

            val isDeleted = dbHelper.deleteCategory(categoryIdToDelete)
            if (category != null) {
                FirebaseSync.deleteCategory(this, category.id)
            }
            if (isDeleted) {
                refreshView()
                // Start the new activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                // Finish the current activity
                alertDialog.dismiss()
                this.finish()

            } else {
                // Deletion failed
                // Handle the error
            }
        }

        //update the category name and goal
        updateButton.setOnClickListener {
            val name = newCategoryName.text.toString().trim()
            val goal = newCategoryGoal.text.toString().trim()

            if (name.isNotEmpty() && goal.isNotEmpty()) {

                // Update the category in the database
                val categoryToUpdate = Category(categoryId, name, goal.toInt())
                val isCategoryUpdated = dbHelper.updateCategory(categoryToUpdate)
                if (isCategoryUpdated) {
                    refreshView()
                } else {
                    // Category update failed
                }

                val categoryTitle = findViewById<TextView>(R.id.categoryTitle)
                categoryTitle.text = name

                alertDialog.dismiss()
            } else {
                // Display an error message if any of the fields are empty
                Toast.makeText(view.context, "Please fill in all the fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        alertDialog.show()
    }

    fun addItems(view: View) {
        imageUrl = ""
        pictureAdded = false
        val dialog = AlertDialog.Builder(view.context)
        val inflater = LayoutInflater.from(view.context)
        val dialogView = inflater.inflate(R.layout.item_input_dialog, null)
        dialog.setView(dialogView)

        val addButton: Button = dialogView.findViewById(R.id.add_button)
        val alertDialog = dialog.create()

        val name = dialogView.findViewById<EditText>(R.id.item_input)
        val comment = dialogView.findViewById<EditText>(R.id.comment_input)
        val count = dialogView.findViewById<EditText>(R.id.count_input)
        //var date = dialogView.findViewById<EditText>(R.id.date_input)
        val imageButton = dialogView.findViewById<Button>(R.id.imageButton)

        //image
        imageButton.setOnClickListener {
            // Request permissions if not granted
            if (!arePermissionsGranted()) {
                requestPermissions()
            } else {
                // Create an intent to pick an image from the gallery
                //  val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // Start the activity to pick an image
                pickImageLauncher.launch("image/*")
            }
        }

        addButton.setOnClickListener {
            val itemName = name.text.toString().trim()
            val itemComment = comment.text.toString().trim()
            val itemCount = count.text.toString().trim()
            val url = imageUrl

            val id = itemsList.size + 1

            if (itemName.isNotEmpty() && itemComment.isNotEmpty() && itemCount.isNotEmpty() && pictureAdded) {
                val item =
                    Item(id, categoryId, itemName, itemComment, itemCount.toInt(), url, false)
                itemsList.add(item)

                FirebaseSync.addItem(item)
                FirebaseSync.refreshDatabase(dbHelper)
                refreshView()
                alertDialog.dismiss()
                Achievements.itemCreated(this)
                Toast.makeText(view.context, "Successfully added ${item.name}", Toast.LENGTH_SHORT)
                    .show()

            }
        }
        alertDialog.show()

    }

    override fun onBackPressed() {
        // Do nothing to prevent going back
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun arePermissionsGranted(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, launch the image picker
                pickImageLauncher.launch("image/*")
            } else {
                // Permissions denied, show a message or handle the situation accordingly
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}