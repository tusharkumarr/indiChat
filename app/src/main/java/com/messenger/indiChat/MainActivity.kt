package com.messenger.indiChat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.messenger.indiChat.Activity.HomeActivity
import com.messenger.indiChat.Activity.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main) // splash XML

        val sharedPref = getSharedPreferences("indiChatPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("jwtToken", null)
        val userId = sharedPref.getString("userId", null)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!token.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                // ✅ User has token and userId → go to Home
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // ❌ No valid session → go to Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
