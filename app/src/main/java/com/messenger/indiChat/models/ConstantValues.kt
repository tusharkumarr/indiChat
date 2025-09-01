package com.messenger.indiChat.models

import android.content.Context

object ConstantValues {
    // Use only the host, no scheme
//    const val BASE_URL: String = "indichatbackend.onrender.com"
//
//
//    // Retrofit requires scheme
//    const val RETROFIT_BASE_URL: String = "https://$BASE_URL/"
//
//    // WebSocket/STOMP requires secure websocket scheme
//    const val WEBSOCKET_URL: String = "wss://$BASE_URL/ws/websocket"

    //for local host
    const val BASE_URL: String = "10.0.2.2:8080"
    // Retrofit requires scheme
    const val RETROFIT_BASE_URL: String = "http://$BASE_URL/"

    // WebSocket/STOMP requires secure websocket scheme
    const val WEBSOCKET_URL: String = "ws://$BASE_URL/ws/websocket"

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }
}
