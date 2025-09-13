package com.messenger.indiChat.models

import android.content.Context

object ConstantValues {
    private const val env = "prod" // change to "prod" when needed

    private val BASE_URL: String = when (env) {
        "dev" -> "10.0.2.2:8080"   // Localhost for emulator
        "prod" -> "08a37bb5ab53.ngrok-free.app"
        else -> "08a37bb5ab53.ngrok-free.app"
    }

    // Retrofit requires scheme
    val RETROFIT_BASE_URL: String = when (env) {
        "dev" -> "http://$BASE_URL/"
        else -> "https://$BASE_URL/"
    }

    // WebSocket/STOMP
    val WEBSOCKET_URL: String = when (env) {
        "dev" -> "ws://$BASE_URL/ws/websocket"
        else -> "wss://$BASE_URL/ws/websocket"
    }

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("indiChatPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
