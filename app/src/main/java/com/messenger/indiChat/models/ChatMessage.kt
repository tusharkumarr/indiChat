package com.messenger.indiChat.models

data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    var timestamp: String?,       // server-generated timestamp
    var delivered: Boolean,
    var displayTime: String? = null // client-only display
)

