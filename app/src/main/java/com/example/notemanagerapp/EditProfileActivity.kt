package com.example.notemanagerapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        dbHelper = DBHelper(this)

        val username = intent.getStringExtra("USERNAME") ?: ""
        val usernameField = findViewById<EditText>(R.id.editTextUsername)
        val emailField = findViewById<EditText>(R.id.editTextEmail)
        val newPasswordField = findViewById<EditText>(R.id.editTextNewPassword)
        val confirmPasswordField = findViewById<EditText>(R.id.editTextConfirmPassword)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val userData = dbHelper.getUserData(username)
        if (userData != null) {
            usernameField.setText(userData.first)
            emailField.setText(userData.second)
        }

        saveButton.setOnClickListener {
            val newUsername = usernameField.text.toString().trim()
            val newEmail = emailField.text.toString().trim()
            val newPassword = newPasswordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val isUpdated = dbHelper.updateUser(username, newUsername, newEmail, newPassword)

                if (isUpdated) {
                    Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("NEW_USERNAME", newUsername)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error updating data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

