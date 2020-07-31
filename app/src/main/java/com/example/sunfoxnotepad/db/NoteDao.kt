package com.example.sunfoxnotepad.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sunfoxnotepad.model.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun addNote(note: Note):Long

    @Update
    suspend fun updateNote(note: Note):Int

    @Delete
    suspend fun deleteNote(note: Note):Int

    @Query("DELETE FROM note_table")
    suspend fun deleteAll():Int

    @Query("SELECT * FROM note_table")
    fun getNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE count IN (:noteCount)")
    suspend fun getNoteById(noteCount:Int):Note

    @Query("SELECT * FROM note_table WHERE note_content LIKE (:content)")
    suspend fun getNoteByContent(content:String):Note
}