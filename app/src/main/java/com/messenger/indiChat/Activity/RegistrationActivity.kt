package com.messenger.indiChat.Activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.messenger.indiChat.R
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editMobile: EditText
    private lateinit var editPassword: EditText
    private lateinit var editConfirmPassword: EditText
    private lateinit var editDob: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        editName = findViewById(R.id.editName)
        editMobile = findViewById(R.id.editMobile)
        editPassword = findViewById(R.id.editPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)
        editDob = findViewById(R.id.editDob)
        btnRegister = findViewById(R.id.btnRegister)

        // Handle DOB picker
        editDob.setOnClickListener { showDatePicker() }

        btnRegister.setOnClickListener {
            val name = editName.text.toString().trim()
            val mobile = editMobile.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val confirmPassword = editConfirmPassword.text.toString().trim()
            val dob = editDob.text.toString().trim()

            if (name.isEmpty() || mobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mobile.length != 10) {
                Toast.makeText(this, "Enter valid 10-digit mobile", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Send OTP to mobile here
            Toast.makeText(this, "OTP sent to $mobile", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, OtpVerificationActivity::class.java)
            intent.putExtra("mobileNumber", mobile)
            intent.putExtra("name", name)
            intent.putExtra("dob", dob)
            intent.putExtra("password", password)
            startActivity(intent)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                editDob.setText(format.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis() // prevent future DOB
        datePicker.show()
    }
}
