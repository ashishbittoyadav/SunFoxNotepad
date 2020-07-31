package com.example.sunfoxnotepad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sunfoxnotepad.db.NoteDao
import com.example.sunfoxnotepad.repository.NoteRepository

class NoteViewModelFactory(val repository: NoteRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteViewModel(repository) as T
    }
}