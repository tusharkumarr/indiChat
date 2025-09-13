package com.messenger.indiChat.Activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.messenger.indiChat.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val profileName = findViewById<TextView>(R.id.profileName)
        val profilePhone = findViewById<TextView>(R.id.profilePhone)

        // Example: load user data from SharedPreferences
        val sharedPref = getSharedPreferences("indiChatPrefs", MODE_PRIVATE)
        val name = sharedPref.getString("name", "John Doe")
        val phone = sharedPref.getString("phoneNumber", "9999999999")

        profileName.text = name
        profilePhone.text = phone

        // Bottom Sheet (LinearLayout in XML)
        val bottomSheet = findViewById<LinearLayout>(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Show part of it initially
        bottomSheetBehavior.peekHeight = 200
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // Optional: listen for state changes
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: android.view.View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // Fully expanded
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // Collapsed
                    }
                }
            }

            override fun onSlide(bottomSheet: android.view.View, slideOffset: Float) {
                // Animate things based on slide offset if needed
            }
        })
    }
}
