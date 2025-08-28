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

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var chatWebSocketManager: ChatWebSocketManager

    private val currentUserId = "user2"
    private val receiverId = "user1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        val editText = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.buttonSend)

        chatAdapter = ChatAdapter(messages, currentUserId) // pass current user id to adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Initialize WebSocket manager
        chatWebSocketManager = ChatWebSocketManager { msg ->
            runOnUiThread {
                val index = messages.indexOfFirst { it.id == msg.id }
                if (index != -1) {
                    // Update message with server timestamp
                    messages[index].timestamp = msg.timestamp
                    messages[index].displayTime = msg.displayTime ?: msg.timestamp
                    chatAdapter.notifyItemChanged(index)
                } else {
                    // New message from other user
                    messages.add(msg)
                    chatAdapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }

        chatWebSocketManager.connect(username = currentUserId)

        sendButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotBlank()) {
                val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val messageId = UUID.randomUUID().toString()

                // Create message with initial null timestamp
                val msg = ChatMessage(
                    id = messageId,
                    senderId = currentUserId,
                    receiverId = receiverId,
                    message = text,
                    timestamp = null, // server will set
                    delivered = false
                )
                msg.displayTime = now

                // Show immediately in SENT style
                messages.add(msg)
                chatAdapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                editText.text.clear()

                chatWebSocketManager.sendMessage(msg)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatWebSocketManager.disconnect()
    }
}
