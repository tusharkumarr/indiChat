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

    private val currentUserId = "user1"
    private val receiverId = "user2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        val editText = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.buttonSend)

        chatAdapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        chatWebSocketManager = ChatWebSocketManager { msg ->
            runOnUiThread {
                messages.add(msg)
                chatAdapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }


        chatWebSocketManager.connect(username = currentUserId)

        sendButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotBlank()) {
                // Local display time
                val displayTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                // Prepare message to send (no timestamp!)
                val msg = ChatMessage(currentUserId, receiverId, text, null, true)

                // Show immediately with local display time
                msg.displayTime = displayTime  // add a new field 'displayTime' in ChatMessage for client-side use
                messages.add(msg)
                chatAdapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                editText.text.clear()

                chatWebSocketManager.sendMessage(msg) // server will set proper timestamp
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        chatWebSocketManager.disconnect()
    }
}
