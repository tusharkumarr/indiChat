package com.messenger.indiChat.models

object ConstantValues {
    // Use only the host, no scheme
    const val BASE_URL: String = "indichatbackend.onrender.com"

    // Retrofit requires scheme
    const val RETROFIT_BASE_URL: String = "https://$BASE_URL/"

    // WebSocket/STOMP requires secure websocket scheme
    const val WEBSOCKET_URL: String = "wss://$BASE_URL/ws/websocket"
}
