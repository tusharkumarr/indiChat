package com.messenger.indiChat.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.messenger.indiChat.models.ChatMessage
import com.messenger.indiChat.models.ConstantValues
import io.reactivex.disposables.CompositeDisposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class ChatWebSocketManager(
    private val onMessageReceived: (ChatMessage) -> Unit
) {
    private var stompClient: StompClient? = null
    private val gson = Gson()
    private val disposables = CompositeDisposable()

    companion object { private const val TAG = "ChatWebSocketManager" }

    fun connect() {
        val url = ConstantValues.WEBSOCKET_URL
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        stompClient?.connect()

        stompClient?.lifecycle()?.subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d(TAG, "Connected to STOMP ✅")
                    subscribeToMessages()
                }
                LifecycleEvent.Type.ERROR -> Log.e(TAG, "STOMP connection error", event.exception)
                LifecycleEvent.Type.CLOSED -> Log.d(TAG, "STOMP disconnected ✅")
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.e(TAG, "Server heartbeat failed ❌")
            }
        }?.let { disposables.add(it) }
    }

    private fun subscribeToMessages() {
        stompClient?.topic("/user/queue/messages")?.subscribe({ stompMessage ->
            try {
                val msg = gson.fromJson(stompMessage.payload, ChatMessage::class.java)
                onMessageReceived(msg)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing message", e)
            }
        }, { error ->
            Log.e(TAG, "Error subscribing to messages", error)
        })?.let { disposables.add(it) }
    }

    fun sendMessage(message: ChatMessage) {
        val json = gson.toJson(message)
        stompClient?.send("/app/chat.send", json)?.subscribe({
            Log.d(TAG, "Message sent ✅")
        }, { error ->
            Log.e(TAG, "Send error", error)
        })?.let { disposables.add(it) }
    }

    fun disconnect() {
        stompClient?.disconnect()
        disposables.clear()
        Log.d(TAG, "STOMP disconnected ✅")
    }
}
