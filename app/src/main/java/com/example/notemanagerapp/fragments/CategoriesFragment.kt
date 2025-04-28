package com.example.notemanagerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.notemanagerapp.CategoryAdapter
import com.example.notemanagerapp.DBHelper
import com.example.notemanagerapp.R

class CategoriesFragment : Fragment() {
    private lateinit var dbHelper: DBHelper
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoriesListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        dbHelper = DBHelper(requireContext())
        categoriesListView = view.findViewById(R.id.listViewCategories)

        loadCategories()

        val addButton = view.findViewById<Button>(R.id.buttonAddCategory)
        val categoryInput = view.findViewById<EditText>(R.id.editTextCategoryName)

        addButton.setOnClickListener {
            val categoryName = categoryInput.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                dbHelper.addCategory(categoryName)
                Toast.makeText(requireContext(), "Category added", Toast.LENGTH_SHORT).show()
                loadCategories()
                categoryInput.text.clear()
            } else {
                Toast.makeText(requireContext(), "Enter category name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadCategories() {
        val categories = dbHelper.getAllCategories()
        categoryAdapter = CategoryAdapter(
            requireContext(),
            categories,
            deleteListener = { category ->
                dbHelper.deleteCategory(category.id)
                Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show()
                loadCategories()
            },
            clickListener = { category ->
                val fragment = NotesFragment().apply {
                    arguments = Bundle().apply {
                        putInt("CATEGORY_ID", category.id)
                        putString("CATEGORY_NAME", category.name)
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )
        categoriesListView.adapter = categoryAdapter
    }
}
