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
import com.messenger.indiChat.Adapter.SuggestionAdapter
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

    private lateinit var suggestionsRecyclerView: RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter

    private lateinit var chatWebSocketManager: ChatWebSocketManager

    private lateinit var receiverId: String
    private lateinit var receiverName: String
    private lateinit var currentUserId: String

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

        // ✅ Suggestions RecyclerView
        suggestionsRecyclerView = findViewById(R.id.suggestionsRecyclerView)
        suggestionAdapter = SuggestionAdapter { suggestion ->
            sendMessage(suggestion)
            suggestionsRecyclerView.visibility = RecyclerView.GONE
        }
        suggestionsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        suggestionsRecyclerView.adapter = suggestionAdapter

        chatTitle.text = receiverName
        backButton.setOnClickListener { finish() }

        // ✅ Chat messages RecyclerView
        chatAdapter = ChatAdapter(messages, currentUserId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        chatRepository = ChatRepository(RetrofitClient.chatApi(this))
        fetchPreviousMessages()

        // ✅ WebSocket manager
        chatWebSocketManager = ChatWebSocketManager(currentUserId) { msg ->
            runOnUiThread {
                handleIncomingMessage(msg)
                updateSuggestions(msg)
            }
        }
        chatWebSocketManager.connect()

        // ✅ Send button click
        sendButton.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotBlank()) {
                sendMessage(text)
                editText.text.clear()
            }
        }
    }

    private fun sendMessage(text: String) {
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

        chatWebSocketManager.sendMessage(msg)
    }

    private fun fetchPreviousMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val list = chatRepository.getMessages(receiverId)
                val sortedList = list.sortedWith(compareBy { it.timestamp ?: "" })
                runOnUiThread {
                    messages.addAll(sortedList)
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)

                    // Trigger suggestions for last message
                    if (sortedList.isNotEmpty()) {
                        val lastMsg = sortedList.last()
                        updateSuggestions(lastMsg)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@ChatActivity,
                        "Failed to fetch messages",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleIncomingMessage(msg: ChatMessage) {
        val index = messages.indexOfFirst { it.id == msg.id }
        if (index != -1) {
            messages[index].timestamp = msg.timestamp
            messages[index].displayTime = msg.displayTime ?: formatTime(msg.timestamp)
            chatAdapter.notifyItemChanged(index)
        } else {
            msg.displayTime = msg.displayTime ?: formatTime(msg.timestamp)
            messages.add(msg)
            chatAdapter.notifyItemInserted(messages.size - 1)
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    // ✅ Suggestion system (dummy for now)
    private fun updateSuggestions(msg: ChatMessage) {
        if (msg.senderId != currentUserId) {
            val suggestions = getDummySuggestions(msg)
            showSuggestions(suggestions)
        } else {
            suggestionsRecyclerView.visibility = RecyclerView.GONE
        }
    }

    /**
     * Returns dummy suggestions for now.
     * Replace this with backend API call later.
     */
    private fun getDummySuggestions(msg: ChatMessage): List<String> {
        return listOf(
            "Sure!",
            "I will check and get back to you.",
            "Thanks for the info!",
            "Can you explain more?"
        )
    }

    private fun showSuggestions(suggestions: List<String>) {
        if (suggestions.isNotEmpty()) {
            suggestionsRecyclerView.visibility = RecyclerView.VISIBLE
            suggestionAdapter.submitList(suggestions)
        } else {
            suggestionsRecyclerView.visibility = RecyclerView.GONE
        }
    }

    private fun formatTime(timestamp: String?): String {
        return try {
            if (timestamp != null) {
                val parsed =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(timestamp)
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
