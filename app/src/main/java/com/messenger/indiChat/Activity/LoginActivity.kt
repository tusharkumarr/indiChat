package com.messenger.indiChat.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.messenger.indiChat.R
import com.messenger.indiChat.models.LoginRequest
import com.messenger.indiChat.network.RetrofitClient
import com.messenger.indiChat.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var editUserId: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editUserId = findViewById(R.id.editUserId)
        editPassword = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        // init repository
        repository = AuthRepository(RetrofitClient.authApi(this))

        btnLogin.setOnClickListener {
            val userId = editUserId.text.toString().trim()
            val password = editPassword.text.toString().trim()
            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginUser(userId, password)
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun loginUser(userId: String, password: String) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val loginData = withContext(Dispatchers.IO) {
                    repository.login(LoginRequest(userId, password))
                }

                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true

                if (loginData != null) {
                    val sharedPref = getSharedPreferences("indiChatPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("isLoggedIn", true)
                        putString("userId", userId)
                        putString("jwtToken", loginData.token)
                        apply()
                    }

                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${loginData?.message ?: "Invalid credentials"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
