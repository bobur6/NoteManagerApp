package com.example.notemanagerapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.notemanagerapp.models.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var listView: ListView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var notesList: MutableList<Note>
    private var isSortedAlphabetically = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        dbHelper = DBHelper(this)
        listView = findViewById(R.id.listViewNotes)
        val searchField = findViewById<EditText>(R.id.editTextSearch)
        val sortButton = findViewById<Button>(R.id.buttonSort)
        val fabAddNote = findViewById<FloatingActionButton>(R.id.fabAddNote)

        loadNotes()

        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterNotes(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        sortButton.setOnClickListener {
            isSortedAlphabetically = !isSortedAlphabetically
            sortNotes()
        }


        fabAddNote.setOnClickListener {
            startActivity(Intent(this, AddEditNoteActivity::class.java))
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val note = notesList[position]
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra("NOTE_ID", note.id)
            intent.putExtra("NOTE_TITLE", note.title)
            intent.putExtra("NOTE_CONTENT", note.content)
            intent.putExtra("NOTE_CATEGORY_ID", note.categoryId)
            startActivity(intent)
        }
    }

    private fun loadNotes() {
        val rawNotes = dbHelper.getAllNotes()

        notesList = rawNotes.toMutableList()

        notesAdapter = NotesAdapter(this, notesList)
        listView.adapter = notesAdapter
    }


    private fun filterNotes(query: String) {
        val filteredNotes = notesList.filter {
            it.title.contains(query, ignoreCase = true)
        }
        notesAdapter.updateList(filteredNotes)
    }

    private fun sortNotes() {
        if (isSortedAlphabetically) {
            notesList.sortBy { it.title }
        } else {
            notesList.sortByDescending { it.date }
        }
        notesAdapter.updateList(notesList)
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}
