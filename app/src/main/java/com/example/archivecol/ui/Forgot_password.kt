package com.example.archivecol.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.archivecol.R
import com.google.firebase.auth.FirebaseAuth

class Forgot_password : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)


        val emailEditText = findViewById<EditText>(R.id.email_reset)
        val button =findViewById<Button>(R.id.reset_password_btn)

        button.setOnClickListener{
            val email = emailEditText.text.toString()
            if(email.isNotEmpty()){
                sendEmail(email)
            }else{
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun sendEmail(email : String){

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    // Navigate back to login page
                    val intent = Intent(this, LogInPage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Password reset email send failed
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }


    }
}