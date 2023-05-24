package com.example.archivecol.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MyDatabase"

        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CATEGORY_ID = "_id"
        const val COLUMN_CATEGORY_NAME = "name"
        const val COLUMN_CATEGORY_GOAL = "goal"

        const val TABLE_ITEMS = "items"
        const val COLUMN_ITEM_ID = "_id"
        const val COLUMN_ITEM_CATEGORY_ID = "category_id"
        const val COLUMN_ITEM_NAME = "name"
        const val COLUMN_ITEM_DESCRIPTION = "description"
        const val COLUMN_ITEM_COUNT = "count"
        const val COLUMN_ITEM_PHOTO_PATH = "photo_path"
        const val COLUMN_ITEM_IS_COLLECTED = "is_collected"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CATEGORIES_TABLE = "CREATE TABLE $TABLE_CATEGORIES(" +
                "$COLUMN_CATEGORY_ID INTEGER PRIMARY KEY," +
                "$COLUMN_CATEGORY_NAME TEXT," +
                "$COLUMN_CATEGORY_GOAL INTEGER)"
        db.execSQL(CREATE_CATEGORIES_TABLE)

        val CREATE_ITEMS_TABLE = "CREATE TABLE $TABLE_ITEMS(" +
                "$COLUMN_ITEM_ID INTEGER PRIMARY KEY," +
                "$COLUMN_ITEM_CATEGORY_ID INTEGER," +
                "$COLUMN_ITEM_NAME TEXT," +
                "$COLUMN_ITEM_DESCRIPTION TEXT," +
                "$COLUMN_ITEM_COUNT INTEGER," +
                "$COLUMN_ITEM_PHOTO_PATH TEXT," +
                "$COLUMN_ITEM_IS_COLLECTED INTEGER," +
                "FOREIGN KEY($COLUMN_ITEM_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID))"
        db.execSQL(CREATE_ITEMS_TABLE)

        // Preload data
        preloadData(db)
    }

    fun addCategory(category: Category): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_CATEGORY_ID, category.id)
        values.put(COLUMN_CATEGORY_NAME, category.name)
        values.put(COLUMN_CATEGORY_GOAL, category.goal)

        val id = db.insert(TABLE_CATEGORIES, null, values)

        db.close()

        return id
    }

    @SuppressLint("Range")
    fun getAllCategories(): List<Category> {
        val categories = ArrayList<Category>()

        // SQL query to retrieve all categories
        val selectQuery = "SELECT * FROM $TABLE_CATEGORIES"

        // Get a readable database
        val db = this.readableDatabase

        // Execute the query
        val cursor = db.rawQuery(selectQuery, null)

        // Loop through the cursor and add categories to the list
        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)),
                    goal = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_GOAL))
                )
                categories.add(category)
            } while (cursor.moveToNext())
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        // Return the list of categories
        return categories
    }

    @SuppressLint("Range")
    fun getAllItems(): List<Item> {
        val items = ArrayList<Item>()

        // SQL query to retrieve all items
        val selectQuery = "SELECT * FROM $TABLE_ITEMS"

        // Get a readable database
        val db = this.readableDatabase

        // Execute the query
        val cursor = db.rawQuery(selectQuery, null)

        // Loop through the cursor and add items to the list
        if (cursor.moveToFirst()) {
            do {
                val item = Item(
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_ID)),
                    categoryId = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_CATEGORY_ID)),
                    name = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME)),
                    description = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_DESCRIPTION)),
                    count = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_COUNT)),
                    photoPath = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_PHOTO_PATH)),
                    isCollected = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_IS_COLLECTED)) == 1
                )
                items.add(item)
            } while (cursor.moveToNext())
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        // Return the list of items
        return items
    }

    fun addItem(item: Item): Boolean {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_ITEM_ID, item.id)
            put(COLUMN_ITEM_CATEGORY_ID, item.categoryId)
            put(COLUMN_ITEM_NAME, item.name)
            put(COLUMN_ITEM_DESCRIPTION, item.description)
            put(COLUMN_ITEM_COUNT, item.count)
            put(COLUMN_ITEM_PHOTO_PATH, item.photoPath)
            put(COLUMN_ITEM_IS_COLLECTED, item.isCollected)
        }

        val id = db.insert(TABLE_ITEMS, null, values)
        db.close()

        return id != -1L
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }

    private fun preloadData(db: SQLiteDatabase) {
        // Preload categories
        val categories = listOf(
            Category(1, "Books", 10),
            Category(2, "Movies", 5),
            Category(3, "Music", 20)
        )
        for (category in categories) {
            val values = ContentValues()
            values.put(COLUMN_CATEGORY_ID, category.id)
            values.put(COLUMN_CATEGORY_NAME, category.name)
            values.put(COLUMN_CATEGORY_GOAL, category.goal)
            db.insert(TABLE_CATEGORIES, null, values)
        }

        // Preload items
        val items = listOf(
            Item(1, 1, "The Great Gatsby", "A novel by F. Scott Fitzgerald", null, null, false),
            Item(2, 1, "To Kill a Mockingbird", "A novel by Harper Lee", null, null, false),
            Item(
                3,
                2,
                "The Godfather",
                "A film directed by Francis Ford Coppola",
                null,
                null,
                false
            ),
            Item(
                4,
                2,
                "The Shawshank Redemption",
                "A film directed by Frank Darabont",
                null,
                null,
                false
            ),
            Item(5, 3, "Thriller", "An album by Michael Jackson", null, null, false),
            Item(6, 3, "The Beatles - 1", "An album by The Beatles", null, null, false)
        )
        for (item in items) {
            val values = ContentValues()
            values.put(COLUMN_ITEM_ID, item.id)
            values.put(COLUMN_ITEM_CATEGORY_ID, item.categoryId)
            values.put(COLUMN_ITEM_NAME, item.name)
            values.put(COLUMN_ITEM_DESCRIPTION, item.description)
            values.put(COLUMN_ITEM_COUNT, item.count)
            values.put(COLUMN_ITEM_PHOTO_PATH, item.photoPath)
            values.put(COLUMN_ITEM_IS_COLLECTED, item.isCollected)
            db.insert(TABLE_ITEMS, null, values)
        }

    }

    @SuppressLint("Range")
    fun getItemsForCategory(id: Int): List<Item> {
        val items = mutableListOf<Item>()
        val selectQuery =
            "SELECT * FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_CATEGORY_ID = $id"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val item = Item(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_ID)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_CATEGORY_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_COUNT)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_PHOTO_PATH)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_IS_COLLECTED)) != 0
                )
                items.add(item)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return items
    }

    @SuppressLint("Range")
    fun getCategoryById(id: Int): Category? {
        val selectQuery = "SELECT * FROM $TABLE_CATEGORIES WHERE $COLUMN_CATEGORY_ID = $id"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var category: Category? = null

        if (cursor.moveToFirst()) {
            category = Category(
                cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_GOAL))
            )
        }

        cursor.close()
        db.close()

        return category
    }

    @SuppressLint("Range")
    fun getItemById(id: Int): Item? {
        val selectQuery = "SELECT * FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_ID = $id"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var item: Item? = null

        if (cursor.moveToFirst()) {
            item = Item(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_COUNT)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_PHOTO_PATH)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_IS_COLLECTED)) != 0
            )
        }

        cursor.close()
        db.close()

        return item
    }

    fun updateCategory(category: Category): Boolean {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_CATEGORY_NAME, category.name)
        values.put(COLUMN_CATEGORY_GOAL, category.goal)

        val affectedRows = db.update(
            TABLE_CATEGORIES,
            values,
            "$COLUMN_CATEGORY_ID = ?",
            arrayOf(category.id.toString())
        )

        db.close()

        return affectedRows > 0
    }

    fun updateItem(item: Item): Boolean {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(COLUMN_ITEM_NAME, item.name)
        values.put(COLUMN_ITEM_DESCRIPTION, item.description)
        values.put(COLUMN_ITEM_COUNT, item.count)
        values.put(COLUMN_ITEM_PHOTO_PATH, item.photoPath)
        values.put(COLUMN_ITEM_IS_COLLECTED, if (item.isCollected) 1 else 0)

        val affectedRows = db.update(
            TABLE_ITEMS,
            values,
            "$COLUMN_ITEM_ID = ?",
            arrayOf(item.id.toString())
        )

        db.close()

        return affectedRows > 0
    }

    fun deleteCategory(categoryId: Int): Boolean {
        val db = this.writableDatabase

        // Delete the items associated with the category
        db.delete(TABLE_ITEMS, "$COLUMN_ITEM_CATEGORY_ID = ?", arrayOf(categoryId.toString()))

        // Delete the category
        val rowsAffected =
            db.delete(TABLE_CATEGORIES, "$COLUMN_CATEGORY_ID = ?", arrayOf(categoryId.toString()))

        db.close()

        return rowsAffected > 0
    }


}

