package com.messenger.indiChat.models

// Login
data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)

// Signup
data class SignupRequest(
    val name: String,
    val phoneNumber: String,
    val password: String,
    val dob: String
)

data class SignupResponse(
    val success: Boolean,
    val message: String
)
