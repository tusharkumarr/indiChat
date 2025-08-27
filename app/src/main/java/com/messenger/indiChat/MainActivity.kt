package com.messenger.indiChat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.models.ChatMessage
import android.widget.EditText
import android.widget.ImageButton
import com.messenger.indiChat.Adapter.ChatAdapter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.chatRecyclerView)
        val editMessage = findViewById<EditText>(R.id.inputMessage)
        val sendButton = findViewById<ImageButton>(R.id.buttonSend)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample Data
        messages.add(ChatMessage("Hello!", false, "10:00 AM"))
        messages.add(ChatMessage("Hi, how are you?", true, "10:01 AM"))
        messages.add(ChatMessage("I'm good! You?", false, "10:02 AM"))
        messages.add(ChatMessage("I’m doing good as well", true, "10:02 AM"))

        adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter

        // Scroll to bottom initially
        recyclerView.scrollToPosition(messages.size - 1)

        // Send button click listener
        sendButton.setOnClickListener {
            val text = editMessage.text.toString().trim()
            if (text.isNotEmpty()) {

                // ✅ Get current time in HH:mm a format (e.g. 10:45 AM)
                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val currentTime = sdf.format(Date())

                val newMessage = ChatMessage(text, true, currentTime)
                messages.add(newMessage)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                editMessage.text.clear()
            }
        }
    }
}

