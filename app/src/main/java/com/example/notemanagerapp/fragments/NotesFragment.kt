package com.example.notemanagerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.notemanagerapp.AddEditNoteActivity
import com.example.notemanagerapp.DBHelper
import com.example.notemanagerapp.R
import com.example.notemanagerapp.NotesAdapter
import com.example.notemanagerapp.models.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotesFragment : Fragment() {
    private lateinit var dbHelper: DBHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var notesListView: ListView
    private lateinit var notesList: MutableList<Note>
    private lateinit var searchEditText: EditText
    private lateinit var sortButton: Button
    private var categoryId: Int? = null
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryId = arguments?.getInt("CATEGORY_ID")
        categoryName = arguments?.getString("CATEGORY_NAME")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        dbHelper = DBHelper(requireContext())
        notesListView = view.findViewById(R.id.listViewNotes)
        val fabAddNote = view.findViewById<FloatingActionButton>(R.id.fabAddNote)
        searchEditText = view.findViewById(R.id.editTextSearch)
        sortButton = view.findViewById(R.id.buttonSort)

        categoryName?.let { name ->
            activity?.title = "Notes: $name"
        }

        loadNotes()

        fabAddNote.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditNoteActivity::class.java))
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterNotes(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        var isSortedAlphabetically = false
        sortButton.setOnClickListener {
            isSortedAlphabetically = !isSortedAlphabetically
            if (isSortedAlphabetically) {
                notesList.sortBy { it.title }
            } else {
                notesList.sortByDescending { it.date }
            }
            notesAdapter.notifyDataSetChanged()
        }

        return view
    }

    private fun loadNotes() {
        val notes = if (categoryId != null) {
            dbHelper.getNotesByCategory(categoryId!!)
        } else {
            dbHelper.getAllNotes()
        }
        
        notesList = notes.toMutableList()
        notesAdapter = NotesAdapter(requireContext(), notesList)
        notesListView.adapter = notesAdapter
    }

    private fun filterNotes(query: String) {
        val allNotes = if (categoryId != null) {
            dbHelper.getNotesByCategory(categoryId!!)
        } else {
            dbHelper.getAllNotes()
        }
        
        val filteredList = if (query.isEmpty()) {
            allNotes
        } else {
            allNotes.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
        
        notesList.clear()
        notesList.addAll(filteredList)
        notesAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }
}
