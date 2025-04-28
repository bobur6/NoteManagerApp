package com.example.notemanagerapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notemanagerapp.models.Category
import java.text.SimpleDateFormat
import java.util.*

class AddEditNoteActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var titleField: EditText
    private lateinit var contentField: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button
    private var noteId: Int? = null
    private var categoryId: Int? = null
    private lateinit var categories: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        dbHelper = DBHelper(this)
        titleField = findViewById(R.id.editTextTitle)
        contentField = findViewById(R.id.editTextContent)
        categorySpinner = findViewById(R.id.spinnerCategory)
        saveButton = findViewById(R.id.buttonSave)

        setupCategorySpinner()
        loadNoteIfExists()

        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun setupCategorySpinner() {
        categories = dbHelper.getAllCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun loadNoteIfExists() {
        noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId != -1) {
            val noteTitle = intent.getStringExtra("NOTE_TITLE")
            val noteContent = intent.getStringExtra("NOTE_CONTENT")
            categoryId = intent.getIntExtra("NOTE_CATEGORY_ID", -1)

            titleField.setText(noteTitle)
            contentField.setText(noteContent)

            if (categoryId != -1) {
                val position = categories.indexOfFirst { it.id == categoryId }
                if (position != -1) {
                    categorySpinner.setSelection(position)
                }
            }
        }
    }

    private fun saveNote() {
        val title = titleField.text.toString().trim()
        val content = contentField.text.toString().trim()
        val selectedCategory = categorySpinner.selectedItem as Category
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        if (title.isEmpty()) {
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show()
            return
        }

        val success = if (noteId != -1) {
            dbHelper.updateNote(noteId!!, title, content, date, selectedCategory.id)
        } else {
            dbHelper.addNote(title, content, date, selectedCategory.id) > 0
        }

        if (success) {
            finish()
        } else {
            Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show()
        }
    }
}
