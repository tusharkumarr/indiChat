package com.messenger.indiChat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.Adapter.ChatAdapter
import com.messenger.indiChat.models.ChatMessage
import com.messenger.indiChat.network.ChatWebSocketManager
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), ChatWebSocketManager.ChatListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var webSocketManager: ChatWebSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.chatRecyclerView)
        val editMessage = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.buttonSend)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter

        // Initialize WebSocket Manager
        webSocketManager = ChatWebSocketManager(this)
        webSocketManager.connect()

        sendButton.setOnClickListener {
            val text = editMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                webSocketManager.sendMessage(text)
                val newMessage = ChatMessage(text, true, getCurrentTime())
                messages.add(newMessage)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                editMessage.text.clear()
            }
        }
    }

    override fun onDestroy() {
        webSocketManager.disconnect()
        super.onDestroy()
    }

    // ChatWebSocketManager.ChatListener implementation
    override fun onConnected() {
        println("Connected to WebSocket")
    }

    override fun onDisconnected() {
        println("Disconnected from WebSocket")
    }

    override fun onMessageReceived(message: String, isSent: Boolean, timestamp: String?) {
        runOnUiThread {
            val newMessage = ChatMessage(message, isSent, timestamp ?: getCurrentTime())
            messages.add(newMessage)
            adapter.notifyItemInserted(messages.size - 1)
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    override fun onError(error: Throwable?) {
        error?.printStackTrace()
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
