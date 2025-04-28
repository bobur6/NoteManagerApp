package com.example.notemanagerapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.notemanagerapp.models.Note
import java.text.SimpleDateFormat
import java.util.*


class NotesAdapter(private val context: Context, private var notes: List<Note>) : BaseAdapter() {
    override fun getCount(): Int = notes.size
    override fun getItem(position: Int): Note = notes[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)

        val note = notes[position]
        val titleTextView = view.findViewById<TextView>(R.id.textViewTitle)
        val contentTextView = view.findViewById<TextView>(R.id.textViewContent)
        val dateTextView = view.findViewById<TextView>(R.id.textViewDate)
        val deleteButton = view.findViewById<Button>(R.id.buttonDelete)


        titleTextView.text = note.title
        contentTextView.text = note.content
        
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM HH:mm", Locale("ru"))
        val date = inputFormat.parse(note.date)
        dateTextView.text = date?.let { outputFormat.format(it) }

        view.setOnClickListener {
            val intent = Intent(context, AddEditNoteActivity::class.java)
            intent.putExtra("NOTE_ID", note.id)
            intent.putExtra("NOTE_TITLE", note.title)
            intent.putExtra("NOTE_CONTENT", note.content)
            intent.putExtra("NOTE_DATE", note.date)
            intent.putExtra("NOTE_CATEGORY_ID", note.categoryId)
            context.startActivity(intent)
        }

        deleteButton.setOnClickListener {
            val dbHelper = DBHelper(context)
            val result = dbHelper.deleteNote(note.id)

            if (result > 0) {
                notes = notes.filter { it.id != note.id }
                notifyDataSetChanged()
                Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error deleting note", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    fun updateList(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}


