package com.example.sunfoxnotepad.utility

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object Utility {

    const val NOTE_ID="noteId"
    const val POSITION="position"
    const val SUCCESS="success"
    const val CANCEL="Cancel"
    const val SAVE="Save"
    const val DELETE="Delete"
    const val UPDATE="Update"
    const val EMPTY="Empty"

    var COUNT=0

    fun getDateAndTime(timestamp: Long):String{
        val simpleDateFormat = SimpleDateFormat("dd/MM/yy hh:mm a")
        val date = Date(timestamp)
        return simpleDateFormat.format(date).toString()
    }


}