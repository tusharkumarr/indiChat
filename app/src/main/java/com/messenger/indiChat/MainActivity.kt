package com.messenger.indiChat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.Adapter.ChatAdapter
import com.messenger.indiChat.models.ChatMessage
import com.messenger.indiChat.network.ChatWebSocketManager
import com.messenger.indiChat.network.ChatApi
import com.messenger.indiChat.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        val chatTitle = findViewById<TextView>(R.id.chatTitle)
        chatTitle.text = receiverId

        chatAdapter = ChatAdapter(messages, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Fetch previous messages from backend
        fetchPreviousMessages()

        // Initialize WebSocket manager
        chatWebSocketManager = ChatWebSocketManager { msg ->
            runOnUiThread {
                val index = messages.indexOfFirst { it.id == msg.id }
                if (index != -1) {
                    // Update message with server timestamp
                    messages[index].timestamp = msg.timestamp
                    messages[index].displayTime = msg.displayTime ?: formatTime(msg.timestamp)
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
            val text = editText.text.toString().trim()
            if (text.isNotBlank()) {
                val now = formatTime(null)
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
    private fun fetchPreviousMessages() {
        val api = RetrofitClient.getInstance().create(ChatApi::class.java)
        api.getMessages(currentUserId, receiverId).enqueue(object : Callback<List<ChatMessage>> {
            override fun onResponse(call: Call<List<ChatMessage>>, response: Response<List<ChatMessage>>) {
                response.body()?.let { list ->
                    messages.addAll(list.sortedBy { it.timestamp })
                    chatAdapter.notifyDataSetChanged()

                    // Scroll to last message
                    val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }

            override fun onFailure(call: Call<List<ChatMessage>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun formatTime(timestamp: String?): String {
        return if (timestamp != null) {
            try {
                val parsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(timestamp)
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsed)
            } catch (e: Exception) {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            }
        } else {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatWebSocketManager.disconnect()
    }
}
