package com.example.sunfoxnotepad.repository

import com.example.sunfoxnotepad.db.NoteDao
import com.example.sunfoxnotepad.model.Note

class NoteRepository(val noteDao: NoteDao) {
    val notes = noteDao.getNotes()

    suspend fun insertNote(note: Note):Long{
        return noteDao.addNote(note)
    }

    suspend fun updateNote(note: Note):Int{
        return noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note):Int{
        return noteDao.deleteNote(note)
    }

    suspend fun deleteAllNote():Int{
        return noteDao.deleteAll()
    }

    suspend fun getNoteById(count:Int):Note{
        return noteDao.getNoteById(count)
    }

    suspend fun getNoteByContent(content:String):Note{
        return noteDao.getNoteByContent(content)
    }
}