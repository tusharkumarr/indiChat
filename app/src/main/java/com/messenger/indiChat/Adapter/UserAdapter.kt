package com.messenger.indiChat.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.messenger.indiChat.R
import com.messenger.indiChat.models.User
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(
    private val users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.textUserName)
        val lastMessage: TextView = itemView.findViewById(R.id.textLastMessage)
        val lastMessageTime: TextView = itemView.findViewById(R.id.textLastMessageTime)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onUserClick(users[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = user.name
        holder.lastMessage.text = user.lastMessage ?: ""

        // Format lastMessageTime from ISO 8601 to "hh:mm a"
        val formattedTime = user.lastMessageTime?.let { isoString ->
            try {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                val date = isoFormat.parse(isoString)
                val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Example: 06:34 PM
                date?.let { outputFormat.format(it) } ?: ""
            } catch (e: Exception) {
                isoString // fallback
            }
        } ?: ""

        holder.lastMessageTime.text = formattedTime
    }

    override fun getItemCount() = users.size
}
