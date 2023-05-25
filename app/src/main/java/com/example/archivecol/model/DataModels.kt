package com.example.archivecol.model

import android.content.Context
import com.example.archivecol.ui.SignUpPage

data class Category(
    var id: Int = 0,
    var name: String = "",
    val goal: Int = 0
)

data class Item(
    var id: Int = 0,
    var categoryId: Int = 0,
    var name: String = "",
    var description: String = "",
    var count: Int? = 0,
    var photoPath: String? = null,
    var isCollected: Boolean = false
)

class User {
    companion object {
        fun getUserName(context: Context): String {
            val sharedPreferences =
                context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            return sharedPreferences.getString("username", "Loyal User") ?: "Loyal User"
        }

        fun createUser(context: SignUpPage, username: String) {

            val sharedPreferences =
                context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("username", username)
            editor.apply()
        }
    }
}