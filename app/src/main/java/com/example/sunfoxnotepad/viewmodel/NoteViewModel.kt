package com.example.sunfoxnotepad.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunfoxnotepad.firebase.analytics.FirebaseAnalyticsHelper
import com.example.sunfoxnotepad.utility.Event
import com.example.sunfoxnotepad.model.Note
import com.example.sunfoxnotepad.realtimedb.FireBaseRealTimeDataBase
import com.example.sunfoxnotepad.repository.NoteRepository
import com.example.sunfoxnotepad.utility.Utility
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel(), Observable {

    private var isUpdateOrDelete = false

    private lateinit var noteToUpdateOrDelete: Note

    var nodeCount = 0

    private var fireBaseRealTimeDataBase: FireBaseRealTimeDataBase

    val notes = noteRepository.notes

    @Bindable
    val noteDateAndTime = MutableLiveData<String>()

    @Bindable
    val noteContent = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val cancelOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = Utility.SAVE
        cancelOrDeleteButtonText.value = Utility.CANCEL
        fireBaseRealTimeDataBase = FireBaseRealTimeDataBase()
        getRemoteNoteDBNodeCount()
    }

    fun getDataFromRemoteDB() {
        FireBaseRealTimeDataBase().getFireBaseInstance()
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        clearAll()
                        for (n in p0.children) {
                            var note = n.getValue(Note::class.java)
                            if(!note!!.isDeleted!!)
                                populateLocalDB(note!!)
                        }
                    }
                }

            })
    }

    fun populateLocalDB(note: Note) = viewModelScope.launch {
        val newNote = Note()
        newNote.number = note.number
        newNote.content = note.content
        newNote.dateStamp = note.dateStamp
        noteRepository.insertNote(newNote!!)
    }

    private fun getRemoteNoteDBNodeCount() {
        FireBaseRealTimeDataBase().getFireBaseInstance()
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    nodeCount = p0.children.count()
                }

            })
    }


    private fun updateRemoteNoteDB(newNote: Note) {
        FireBaseRealTimeDataBase().getFireBaseInstance()
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var position = 0
                    if (p0.exists()) {
                        for (node in p0.children) {
                            var n = node.getValue(Note::class.java)
                            if (n!!.number == newNote.number) {
                                updateFireBase(p0.children.elementAt(position).key)
                                break
                            }else
                                position++
                        }
                    }
                }

            })
    }

    private fun updateFireBase(key: String?) {
        FireBaseRealTimeDataBase().updateRealTimeDB(noteToUpdateOrDelete, key!!)
    }

    fun updateNote( noteCount: Int) = viewModelScope.launch {
        val noteToUpdate = async(IO) { noteRepository.getNoteById(count = noteCount) }
        initUpdateAndDelete(noteToUpdate.await())
    }

    private suspend fun initUpdateAndDelete(note: Note) {
        delay(500)
        noteContent.value = note.content
        isUpdateOrDelete = true
        noteToUpdateOrDelete = note
        saveOrUpdateButtonText.value = Utility.UPDATE
        cancelOrDeleteButtonText.value = Utility.DELETE
        noteDateAndTime.value = note.dateStamp
    }


    fun saveOrUpdate() {
        if (noteContent.value == null || noteContent.value.equals(" ")){
            statusMessage.value = Event(Utility.EMPTY)
        }
        else {
            if (isUpdateOrDelete) {
                noteToUpdateOrDelete.content = noteContent.value!!
                noteToUpdateOrDelete.dateStamp = Utility.getDateAndTime(System.currentTimeMillis())
                update(noteToUpdateOrDelete)
            } else {
                val content = noteContent.value!!
                insert(
                    Note(
                        nodeCount++,
                        content,
                        Utility.getDateAndTime(System.currentTimeMillis())
                    )
                )
                noteContent.value = null
            }
        }
    }

    fun clearOrDelete() {
        if (isUpdateOrDelete) {
            delete(noteToUpdateOrDelete)
        } else {
            statusMessage.value =
                Event(Utility.CANCEL)
        }
    }

    private fun insert(note: Note) = viewModelScope.launch {
        var newRowId = noteRepository.insertNote(note)
        if (newRowId > -1) {
            fireBaseRealTimeDataBase.insertInFirebase(noteRepository.getNoteByContent(note.content!!))
            statusMessage.value =
                Event(Utility.SUCCESS)
        }
    }

    private fun update(note: Note) = viewModelScope.launch {
        updateRemoteNoteDB(note)
        var numberOfRow = noteRepository.updateNote(note)
        if (numberOfRow > 0) {
            noteContent.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = Utility.SAVE
            cancelOrDeleteButtonText.value = Utility.CANCEL
            statusMessage.value =
                Event(Utility.SUCCESS)
        }
    }

    fun delete(note: Note) = viewModelScope.launch {
        note.isDeleted = true
        noteToUpdateOrDelete = note
        updateRemoteNoteDB(note)
        var numberOfRowDeleted = noteRepository.deleteNote(note)
        if (numberOfRowDeleted > 0) {
            noteContent.value = null
            noteDateAndTime.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = Utility.SAVE
            cancelOrDeleteButtonText.value = Utility.CANCEL
            statusMessage.value =
                Event(Utility.SUCCESS)
        }
    }

    fun clearAll() = viewModelScope.launch {
        noteRepository.deleteAllNote()
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}