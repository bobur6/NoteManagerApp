package com.example.notemanagerapp.models

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val categoryId: Int? = null
)
