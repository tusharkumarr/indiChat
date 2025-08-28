package com.messenger.indiChat.network

import android.util.Log
import com.google.gson.Gson
import com.messenger.indiChat.models.ChatMessage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatWebSocketManager(
    private val onMessageReceived: (ChatMessage) -> Unit
) {

    private var stompClient: StompClient? = null
    private val gson = Gson()
    private val disposables = CompositeDisposable()

    /**
     * Connect to STOMP server with username for private messaging
     */
//    url: String = "ws://10.0.2.2:8080/ws/websocket",
    fun connect(
        url: String = "wss://indichatbackend.onrender.com/ws/websocket",
        username: String
    ) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

        // Pass username header for Spring Principal
        val connectHeaders = listOf(StompHeader("username", username))
        stompClient?.connect(connectHeaders)

        // Subscribe to lifecycle events
        val lifecycleDisposable: Disposable = stompClient!!.lifecycle().subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("ChatWebSocketManager", "Connected to STOMP ✅")
                    subscribeToMessages()
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("ChatWebSocketManager", "STOMP connection error", event.exception)
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("ChatWebSocketManager", "STOMP disconnected ✅")
                }
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                    Log.e("ChatWebSocketManager", "Server heartbeat failed")
                }
            }
        }
        disposables.add(lifecycleDisposable)
    }

    /**
     * Subscribe to private messages for this user
     */
    private fun subscribeToMessages() {
        val disposable: Disposable? = stompClient?.topic("/user/queue/messages")?.subscribe({ stompMessage ->
            try {
                val msg = gson.fromJson(stompMessage.payload, ChatMessage::class.java)
                onMessageReceived(msg)
            } catch (e: Exception) {
                Log.e("ChatWebSocketManager", "Error parsing message", e)
            }
        }, { error ->
            Log.e("ChatWebSocketManager", "Error subscribing", error)
        })
        disposable?.let { disposables.add(it) }
    }

    /**
     * Send message to server
     */
    fun sendMessage(message: ChatMessage) {
        val json = gson.toJson(message)
        val disposable: Disposable? = stompClient?.send("/app/chat.send", json)?.subscribe({
            Log.d("ChatWebSocketManager", "Message sent ✅")
        }, { error ->
            Log.e("ChatWebSocketManager", "Send error", error)
        })
        disposable?.let { disposables.add(it) }
    }

    /**
     * Disconnect and clear subscriptions
     */
    fun disconnect() {
        stompClient?.disconnect()
        disposables.clear()
        Log.d("ChatWebSocketManager", "STOMP disconnected ✅")
    }
}
