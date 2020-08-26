package com.example.sunfoxnotepad.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sunfoxnotepad.R
import com.example.sunfoxnotepad.databinding.ActivityMainBinding
import com.example.sunfoxnotepad.firebase.analytics.FirebaseAnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )

        val intent = intent
        binding.userDetail.text = intent.getStringExtra("user")


        FirebaseAnalyticsHelper.logEvent(application,"item_name","testing",FirebaseAnalytics.Event.LOGIN)

    }
}