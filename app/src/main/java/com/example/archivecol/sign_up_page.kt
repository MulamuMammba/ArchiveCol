package com.example.archivecol

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class sign_up_page : AppCompatActivity() {

    private lateinit var emailText: TextView
    private lateinit var passwordText: TextView
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_page)
        title = "Sign Up"

        // Initialize the views
        emailText = findViewById<TextView>(R.id.email)
        passwordText = findViewById<TextView>(R.id.password)
        signUpButton = findViewById<Button>(R.id.sign_up_btn)

        val auth = Firebase.auth

        val login = findViewById<TextView>(R.id.log_in_btn)
        login.setOnClickListener {
            val intent = Intent(this, log_in_page::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account Creation Successful", Toast.LENGTH_SHORT)
                            .show()
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