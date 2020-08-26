package com.example.sunfoxnotepad.firebase.analytics

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsHelper {

    companion object{

        fun logEvent(application:Application,name:String,value:String,event:String){
            val firebaseAnalytics = FirebaseAnalytics.getInstance(application)
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID,name)
            bundle.putString(FirebaseAnalytics.Param.VALUE,value)
            firebaseAnalytics.logEvent(event,bundle)
        }
    }


}