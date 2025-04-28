package com.example.notemanagerapp.models

data class Category(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}
