package com.messenger.indiChat.network

import android.util.Log
import com.google.gson.Gson
import com.messenger.indiChat.models.ChatMessage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class ChatWebSocketManager(
    private val onMessageReceived: (ChatMessage) -> Unit
) {

    private var stompClient: StompClient? = null
    private val gson = Gson()
    private val disposables = CompositeDisposable() // ✅ Add CompositeDisposable

    fun connect(url: String = "ws://10.0.2.2:8080/ws/websocket") {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        stompClient?.connect()

        // Subscribe to lifecycle events
        val lifecycleDisposable: Disposable = stompClient!!.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("ChatWebSocketManager", "Connected to STOMP ✅")
                    subscribeToMessages()
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("ChatWebSocketManager", "STOMP connection error", lifecycleEvent.exception)
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

        stompClient?.connect() // actual connection
    }

    private fun subscribeToMessages() {
        val disposable: Disposable? = stompClient?.topic("/user/queue/messages")?.subscribe({ stompMessage ->
            val msg = gson.fromJson(stompMessage.payload, ChatMessage::class.java)
            onMessageReceived(msg)
        }, { error ->
            Log.e("ChatWebSocketManager", "Error subscribing", error)
        })
        disposable?.let { disposables.add(it) }

    }

    fun sendMessage(message: ChatMessage) {
        val json = gson.toJson(message)
        val disposable: Disposable? = stompClient?.send("/app/chat.send", json)?.subscribe({
            Log.d("ChatWebSocketManager", "Message sent ✅")
        }, { error ->
            Log.i("ChatWebSocketManager", "Send error", error)
        })
        disposable?.let { disposables.add(it) }
    }

    fun disconnect() {
        stompClient?.disconnect()
        disposables.clear()  // ✅ Clear all subscriptions
        Log.d("ChatWebSocketManager", "STOMP disconnected ✅")
    }
}
