package com.example.archivecol.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.archivecol.R
import com.example.archivecol.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_page)
        title = "Sign Up"

        // Initialize the views
        val emailText: EditText = findViewById(R.id.new_email)
        val passwordText: EditText = findViewById(R.id.password)
        val signUpButton: Button = findViewById(R.id.sign_up_btn)
        val firstName: EditText = findViewById(R.id.first_name)
        val lastName: EditText = findViewById(R.id.last_name)

        val auth = Firebase.auth

        val login = findViewById<TextView>(R.id.log_in_btn)
        login.setOnClickListener {
            val intent = Intent(this, LogInPage::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            val name = firstName.text.toString().trim() + " " + lastName.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account Creation Successful", Toast.LENGTH_SHORT)
                            .show()
                        User.createUser(this, name)

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }

    }
}