package com.example.archivecol.ui

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.archivecol.R
import com.google.firebase.auth.FirebaseAuth

class forgot_password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        val auth = FirebaseAuth.getInstance()
        val emailEditText = findViewById<EditText>(R.id.email)
        val email = emailEditText.text.toString()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    // Navigate back to login page
                    val intent = Intent(this, log_in_page::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Password reset email send failed
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }

    }
}