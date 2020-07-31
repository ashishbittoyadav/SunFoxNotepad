package com.example.sunfoxnotepad.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sunfoxnotepad.model.Note

@Database(entities = [Note::class],version = 1)
abstract class NoteRoomDB: RoomDatabase() {
    abstract val noteDao:NoteDao

    companion object{
        @Volatile
        private var INSTANCE:NoteRoomDB?=null

        fun getInstance(context: Context):NoteRoomDB{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteRoomDB::class.java,
                        "note_data_base"
                    ).build()
                }

                return instance
            }

        }
    }
}