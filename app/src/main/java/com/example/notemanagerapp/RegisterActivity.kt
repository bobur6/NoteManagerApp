package com.example.notemanagerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DBHelper(this)

        val usernameField = findViewById<EditText>(R.id.editTextRegisterUsername)
        val passwordField = findViewById<EditText>(R.id.editTextRegisterPassword)
        val confirmPasswordField = findViewById<EditText>(R.id.editTextRegisterConfirmPassword)
        val emailField = findViewById<EditText>(R.id.editTextRegisterEmail)
        val registerButton = findViewById<Button>(R.id.buttonRegisterConfirm)

        registerButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()
            val email = emailField.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = dbHelper.addUser(username, password, email)
            if (result > 0) {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("password", password)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Registration error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
