package com.example.notemanagerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notemanagerapp.models.Category
import com.example.notemanagerapp.models.Note

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notemanager.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_NAME = "username"
        private const val COLUMN_USER_PASSWORD = "password"
        private const val COLUMN_USER_EMAIL = "email"

        private const val TABLE_CATEGORIES = "categories"
        private const val COLUMN_CATEGORY_ID = "id"
        private const val COLUMN_CATEGORY_NAME = "name"

        private const val TABLE_NOTES = "notes"
        private const val COLUMN_NOTE_ID = "id"
        private const val COLUMN_NOTE_TITLE = "title"
        private const val COLUMN_NOTE_CONTENT = "content"
        private const val COLUMN_NOTE_DATE = "date"
        private const val COLUMN_NOTE_CATEGORY_ID = "category_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_NAME TEXT NOT NULL,
                $COLUMN_USER_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_EMAIL TEXT NOT NULL
            )
        """.trimIndent()

        val createCategoryTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT NOT NULL
            )
        """.trimIndent()

        val createNotesTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTE_TITLE TEXT NOT NULL,
                $COLUMN_NOTE_CONTENT TEXT NOT NULL,
                $COLUMN_NOTE_DATE TEXT NOT NULL,
                $COLUMN_NOTE_CATEGORY_ID INTEGER,
                FOREIGN KEY($COLUMN_NOTE_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID)
            )
        """.trimIndent()

        db?.execSQL(createUserTable)
        db?.execSQL(createCategoryTable)
        db?.execSQL(createNotesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun addUser(username: String, password: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, username)
        values.put(COLUMN_USER_PASSWORD, password)
        values.put(COLUMN_USER_EMAIL, email)
        return db.insert(TABLE_USERS, null, values)
    }


    fun updateUser(oldUsername: String, newUsername: String, newEmail: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("username", newUsername)
            put("email", newEmail)
            put("password", newPassword)  // Update password
        }
        val result = db.update("users", contentValues, "username = ?", arrayOf(oldUsername))
        return result > 0
    }


    fun deleteUser(username: String): Boolean {
        val db = writableDatabase
        try {
            db.beginTransaction()
            
            db.delete(TABLE_NOTES, "$COLUMN_USER_NAME = ?", arrayOf(username))
            
            val result = db.delete(TABLE_USERS, "$COLUMN_USER_NAME = ?", arrayOf(username))
            
            if (result > 0) {
                db.setTransactionSuccessful()
            }
            
            return result > 0
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    fun addCategory(name: String): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CATEGORY_NAME, name)
        return db.insert(TABLE_CATEGORIES, null, values)
    }

    fun getAllCategories(): List<Category> {
        val db = readableDatabase
        val categories = mutableListOf<Category>()
        val cursor = db.rawQuery("SELECT * FROM categories", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                categories.add(Category(id, name))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return categories
    }

    fun deleteCategory(categoryId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_CATEGORIES, "$COLUMN_CATEGORY_ID = ?", arrayOf(categoryId.toString()))
        db.close()
        return result > 0
    }



    fun addNote(title: String, content: String, date: String, categoryId: Int): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTE_TITLE, title)
        values.put(COLUMN_NOTE_CONTENT, content)
        values.put(COLUMN_NOTE_DATE, date)
        values.put(COLUMN_NOTE_CATEGORY_ID, categoryId)
        return db.insert(TABLE_NOTES, null, values)
    }

    fun getAllNotes(): List<Note> {
        val db = readableDatabase
        val list = mutableListOf<Note>()
        val cursor = db.rawQuery("SELECT * FROM notes", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id")) // category_id
                list.add(Note(id, title, content, date, categoryId))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }


    fun updateNote(id: Int, title: String, content: String, date: String, categoryId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("content", content)
            put("date", date)
            put("category_id", categoryId)
        }
        val result = db.update("notes", values, "id=?", arrayOf(id.toString()))
        return result > 0
    }

    fun deleteNote(id: Int): Int {
        val db = writableDatabase
        return db.delete("notes", "id=?", arrayOf(id.toString()))
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_NAME = ? AND $COLUMN_USER_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserData(username: String): Pair<String, String>? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_NAME, COLUMN_USER_EMAIL),
            "$COLUMN_USER_NAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL))
            cursor.close()
            Pair(username, email)
        } else {
            cursor.close()
            null
        }
    }

    fun getNotesByCategory(categoryId: Int): List<Note> {
        val db = readableDatabase
        val list = mutableListOf<Note>()
        val cursor = db.query(
            TABLE_NOTES,
            null,
            "$COLUMN_NOTE_CATEGORY_ID = ?",
            arrayOf(categoryId.toString()),
            null,
            null,
            "$COLUMN_NOTE_DATE DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CONTENT))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_DATE))
                val catId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_CATEGORY_ID))
                list.add(Note(id, title, content, date, catId))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

}
