package com.messenger.indiChat.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.messenger.indiChat.R

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var editOtp: EditText
    private lateinit var btnVerify: Button

    // For testing, assume OTP is "1234"
    private val correctOtp = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        editOtp = findViewById(R.id.editOtp)
        btnVerify = findViewById(R.id.btnVerify)

        val mobile = intent.getStringExtra("mobileNumber") ?: ""

        btnVerify.setOnClickListener {
            val enteredOtp = editOtp.text.toString().trim()

            if (enteredOtp.isEmpty()) {
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredOtp == correctOtp) {
                Toast.makeText(this, "OTP Verified for $mobile", Toast.LENGTH_SHORT).show()
                // Redirect to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
