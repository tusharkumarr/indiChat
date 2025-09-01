// LoginModels.kt
package com.messenger.indiChat.models

data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)
