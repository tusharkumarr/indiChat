package com.messenger.indiChat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.messenger.indiChat.Activity.HomeActivity
import com.messenger.indiChat.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // use your splash XML here

        // Delay for 2 seconds, then go to HomeActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // prevent going back to splash
        }, 2000) // 2000 ms = 2 seconds
    }
}
