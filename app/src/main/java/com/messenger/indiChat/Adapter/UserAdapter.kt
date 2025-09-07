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

        // Format lastMessageTime
        holder.lastMessageTime.text = user.lastMessageTime?.let { isoString ->
            try {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                val date = isoFormat.parse(isoString)

                date?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it

                    val today = Calendar.getInstance()
                    val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }

                    when {
                        isSameDay(calendar, today) -> {
                            // Today: show time
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)
                        }
                        isSameDay(calendar, yesterday) -> {
                            // Yesterday
                            "Yesterday"
                        }
                        else -> {
                            // Older: show date
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                        }
                    }
                } ?: ""
            } catch (e: Exception) {
                isoString // fallback
            }
        } ?: ""
    }

    override fun getItemCount() = users.size

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
