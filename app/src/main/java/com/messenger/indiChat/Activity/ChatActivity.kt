package com.messenger.indiChat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.Adapter.ChatAdapter
import com.messenger.indiChat.models.ChatMessage
import com.messenger.indiChat.network.ChatWebSocketManager
import com.messenger.indiChat.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var chatWebSocketManager: ChatWebSocketManager

    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        currentUserId = sharedPref.getString("userId", "") ?: ""

        receiverId = intent.getStringExtra("userId") ?: ""
        receiverName = intent.getStringExtra("userName") ?: ""

        recyclerView = findViewById(R.id.chatRecyclerView)
        val editText = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.buttonSend)
        val chatTitle = findViewById<TextView>(R.id.chatTitle)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        chatTitle.text = receiverName

        // ✅ Back button functionality
        backButton.setOnClickListener {
            finish() // closes ChatActivity and returns to previous screen
        }

        chatAdapter = ChatAdapter(messages, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        fetchPreviousMessages()

        chatWebSocketManager = ChatWebSocketManager(currentUserId) { msg ->
            runOnUiThread {
                val index = messages.indexOfFirst { it.id == msg.id }
                if (index != -1) {
                    messages[index].timestamp = msg.timestamp
                    messages[index].displayTime = msg.displayTime ?: formatTime(msg.timestamp)
                    chatAdapter.notifyItemChanged(index)
                } else {
                    messages.add(msg)
                    chatAdapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }

        chatWebSocketManager.connect() // ✅ Backend decodes user from JWT

        sendButton.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotBlank()) {
                val now = formatTime(null)
                val messageId = UUID.randomUUID().toString()

                val msg = ChatMessage(
                    id = messageId,
                    senderId = currentUserId,
                    receiverId = receiverId,
                    message = text,
                    timestamp = null,
                    delivered = false
                )
                msg.displayTime = now

                messages.add(msg)
                chatAdapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                editText.text.clear()

                chatWebSocketManager.sendMessage(msg)
            }
        }
    }

    private fun fetchPreviousMessages() {
        val api = RetrofitClient.chatApi(this)
        api.getMessages(receiverId)
            .enqueue(object : Callback<List<ChatMessage>> {
                override fun onResponse(call: Call<List<ChatMessage>>, response: Response<List<ChatMessage>>) {
                    val list = response.body() ?: emptyList()
                    val sortedList = list.sortedWith(compareBy { it.timestamp ?: "" })
                    messages.addAll(sortedList)
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }

                override fun onFailure(call: Call<List<ChatMessage>>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(this@ChatActivity, "Failed to fetch messages", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun formatTime(timestamp: String?): String {
        return try {
            if (timestamp != null) {
                val parsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(timestamp)
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsed)
            } else SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        } catch (e: Exception) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatWebSocketManager.disconnect()
    }
}
