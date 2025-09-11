package com.messenger.indiChat.Adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.R
import com.messenger.indiChat.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val currentUserId: String // pass the current user id
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        private const val VIEW_TYPE_RECEIVED = 0
        private const val VIEW_TYPE_SENT = 1
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.textMessage)
        val timeText: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_SENT
        else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_SENT) {
            R.layout.item_message_sent
        } else {
            R.layout.item_message_received
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val msg = messages[position]
        holder.messageText.text = msg.message

        // Format timestamp if available, else use client displayTime
        val timeToShow = msg.timestamp?.let { serverTimestamp ->
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = parser.parse(serverTimestamp)
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                formatter.format(date ?: Date())
            } catch (e: Exception) {
                msg.displayTime ?: ""
            }
        } ?: msg.displayTime ?: ""

        holder.timeText.text = timeToShow

        holder.itemView.setOnLongClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.chat_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        Toast.makeText(view.context, "Delete message ${msg.id}", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_copy -> {
                        val clipboard = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Chat Message", msg.message)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(view.context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.gravity = Gravity.END
            popup.show()
            true
        }

    }

    override fun getItemCount() = messages.size
}
