package com.example.archivecol.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "MyDatabase"

        private const val TABLE_CATEGORIES = "categories"
        private const val COLUMN_CATEGORY_ID = "_id"
        private const val COLUMN_CATEGORY_NAME = "name"
        private const val COLUMN_CATEGORY_GOAL = "goal"

        private const val TABLE_ITEMS = "items"
        private const val COLUMN_ITEM_ID = "_id"
        private const val COLUMN_ITEM_CATEGORY_ID = "category_id"
        private const val COLUMN_ITEM_DESCRIPTION = "description"
        private const val COLUMN_ITEM_DATE_ACQUIRED = "date_acquired"
        private const val COLUMN_ITEM_PHOTO_PATH = "photo_path"
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
                "$COLUMN_ITEM_DESCRIPTION TEXT," +
                "$COLUMN_ITEM_DATE_ACQUIRED TEXT," +
                "$COLUMN_ITEM_PHOTO_PATH TEXT," +
                "FOREIGN KEY($COLUMN_ITEM_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID))"
        db.execSQL(CREATE_ITEMS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }
}
