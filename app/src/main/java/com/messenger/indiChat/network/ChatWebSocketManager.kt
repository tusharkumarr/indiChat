package com.messenger.indiChat.network

import android.util.Log
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class ChatWebSocketManager(private val listener: ChatListener) {

    private lateinit var stompClient: StompClient
    private var isConnected = false
    private var subscription: Disposable? = null

    fun connect() {
        // Initialize STOMP client (10.0.2.2 is for Android Emulator localhost)
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/ws")

        // Handle lifecycle
        stompClient.lifecycle().subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    isConnected = true
                    Log.d("WebSocket", "Connected")
                    listener.onConnected()
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("WebSocket", "Error", event.exception)
                    listener.onError(event.exception)
                }
                LifecycleEvent.Type.CLOSED -> {
                    isConnected = false
                    Log.d("WebSocket", "Disconnected")
                    listener.onDisconnected()
                }
                else -> {}
            }
        }

        stompClient.connect()

        // Subscribe to messages
        subscription = stompClient.topic("/topic/messages").subscribe({ stompMessage ->
            try {
                val json = JSONObject(stompMessage.payload)
                val message = json.getString("content")
                val isSent = json.getBoolean("delivered")
                val timestamp = json.optString("timestamp")
                listener.onMessageReceived(message, isSent, timestamp)
            } catch (e: Exception) {
                listener.onError(e)
            }
        }, { error ->
            listener.onError(error)
        })
    }

    fun sendMessage(message: String) {
        if (isConnected) {
            val payload = JSONObject()
            payload.put("content", message)
            payload.put("delivered", true)

            stompClient.send("/app/chat.send", payload.toString())
                .subscribe({
                    Log.d("WebSocket", "Message sent: $message")
                }, { error ->
                    listener.onError(error)
                })
        } else {
            Log.w("WebSocket", "Cannot send, not connected")
        }
    }

    fun disconnect() {
        subscription?.dispose()
        stompClient.disconnect()
    }

    interface ChatListener {
        fun onConnected()
        fun onDisconnected()
        fun onMessageReceived(message: String, isSent: Boolean, timestamp: String?)
        fun onError(error: Throwable?)
    }
}
