package com.example.sunfoxnotepad.model
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "note_table")
class Note(){
    constructor(number:Int,content:String,dateStamp:String) : this() {
        this.number = number
        this.content = content
        this.dateStamp = dateStamp
    }
    @PrimaryKey(autoGenerate = true)
    var count:Int?=null
    @ColumnInfo(name = "note_number")
    var number:Int?=null
    @ColumnInfo(name = "note_content")
    var content:String?=null
    @ColumnInfo(name = "note_date")
    var dateStamp:String?=null

    override fun toString(): String {
        return number.toString()+" $content" +
                " $dateStamp"
    }
}