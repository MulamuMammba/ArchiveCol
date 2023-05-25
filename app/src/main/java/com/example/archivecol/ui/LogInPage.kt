package com.example.archivecol.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.archivecol.R
import com.example.archivecol.database.sqlite.DatabaseHelper
import com.example.archivecol.model.User
import com.example.archivecol.database.firebase.FirebaseSync
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInPage : AppCompatActivity() {

    private lateinit var loadingDialog: Dialog
    private val emailText: TextView = findViewById(R.id.email)
    private val passwordText: TextView = findViewById(R.id.password)
    private val logIn: Button = findViewById(R.id.sign_in_btn)
    private val forgotPassword: TextView = findViewById(R.id.reset_password_btn)
    private val signUp: TextView = findViewById(R.id.sign_up_btn)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_page)

        val userName: TextView = findViewById(R.id.name)
        userName.text = User.getUserName(applicationContext)

        login()

        forgotPassword.setOnClickListener {
            val intent = Intent(this, forgotPassword::class.java)
            startActivity(intent)
        }
        signUp.setOnClickListener {
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
        }

    }

    private fun login() {

        val auth = Firebase.auth
        FirebaseSync.refreshDatabase(DatabaseHelper(this))

        logIn.setOnClickListener {
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
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                hideLoadingDialog()
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    this.finish()
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

    private fun showLoadingDialog() {
        loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.loading_dialog)
        loadingDialog.setCancelable(false)

        val textView = loadingDialog.findViewById<TextView>(R.id.loadingMessage)
        textView.text = "Authenticating"

        loadingDialog.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }
}