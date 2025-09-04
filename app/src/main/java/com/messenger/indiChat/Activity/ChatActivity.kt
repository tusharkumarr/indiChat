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
import com.messenger.indiChat.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    // ✅ repository
    private lateinit var chatRepository: ChatRepository

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

        backButton.setOnClickListener { finish() }

        chatAdapter = ChatAdapter(messages, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // ✅ init repository
        chatRepository = ChatRepository(RetrofitClient.chatApi(this))

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

        chatWebSocketManager.connect()

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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val list = chatRepository.getMessages(receiverId) // ✅ use repository
                val sortedList = list.sortedWith(compareBy { it.timestamp ?: "" })
                runOnUiThread {
                    messages.addAll(sortedList)
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@ChatActivity, "Failed to fetch messages", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
