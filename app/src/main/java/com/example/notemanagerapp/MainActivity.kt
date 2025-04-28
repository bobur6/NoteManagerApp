package com.example.notemanagerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.notemanagerapp.fragments.CategoriesFragment
import com.example.notemanagerapp.fragments.NotesFragment
import ProfileFragment

import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.getStringExtra("USERNAME") ?: ""

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        loadFragment(NotesFragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_notes -> NotesFragment()
                R.id.nav_categories -> CategoriesFragment()
                R.id.nav_profile -> {
                    val profileFragment = ProfileFragment()
                    profileFragment.arguments = Bundle().apply {
                        putString("USERNAME", username)
                    }
                    profileFragment
                }
                else -> NotesFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
