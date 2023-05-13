package com.example.archivecol

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class log_in_page : AppCompatActivity() {

    lateinit var loadingDialog: Dialog
    private lateinit var emailText: TextView
    private lateinit var passwordText: TextView
    private lateinit var log_in: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_page)

        title = "Log in"
        val auth = Firebase.auth

        emailText = findViewById(R.id.email)
        passwordText = findViewById(R.id.password)
        log_in = findViewById(R.id.sign_in_btn)

        val signUp = findViewById<TextView>(R.id.sign_up_btn)
        signUp.setOnClickListener {
            val intent = Intent(this, sign_up_page::class.java)
            startActivity(intent)
        }


        fun showLoadingDialog() {
            loadingDialog = Dialog(this)
            loadingDialog.setContentView(R.layout.loading_dialog)
            loadingDialog.setCancelable(false)

            val textView = loadingDialog.findViewById<TextView>(R.id.loadingMessage)
            textView.text = "Authenticating"

            loadingDialog.show()
        }

        fun hideLoadingDialog() {
            loadingDialog.dismiss()
        }


        log_in.setOnClickListener {
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

            showLoadingDialog()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideLoadingDialog()
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
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