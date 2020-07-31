package com.example.sunfoxnotepad.realtimedb

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sunfoxnotepad.model.Note
import com.example.sunfoxnotepad.utility.Utility
import com.google.firebase.database.*
import java.util.HashMap


class FireBaseRealTimeDataBase {
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference.child("data")

    fun updateRealTimeDB(note:Note,key:String){
        myRef.child(key).setValue(note)
    }

    fun insertInFirebase(note: Note){
        var key = myRef.push().key
        var map = HashMap<String,Any>()
        map.put("id",note.count!!)
        map.put("content",note.content!!)
        map.put("dateStamp",note.dateStamp!!)
        myRef.child(key!!).setValue(note)
    }


    fun getFireBaseInstance(): DatabaseReference {
        return myRef
    }

}