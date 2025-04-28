package com.example.notemanagerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import android.widget.ImageButton
import com.example.notemanagerapp.models.Category

class CategoryAdapter(
    private val context: Context,
    private val categories: List<Category>,
    private val deleteListener: (Category) -> Unit,
    private val clickListener: (Category) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = categories.size
    override fun getItem(position: Int): Category = categories[position]
    override fun getItemId(position: Int): Long = categories[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_category, parent, false)

        val category = categories[position]
        val nameTextView = view.findViewById<TextView>(R.id.textViewCategoryName)
        val deleteButton = view.findViewById<ImageButton>(R.id.buttonDeleteCategory)

        nameTextView.text = category.name

        view.setOnClickListener {
            clickListener(category)
        }

        deleteButton.setOnClickListener {
            deleteListener(category)
        }

        return view
    }
}

