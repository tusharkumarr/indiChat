package com.messenger.indiChat.models

data class ChatMessage(
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: String?,       // server-generated timestamp
    val delivered: Boolean,
    var displayTime: String? = null // client-only display
)

