package com.messenger.indiChat.models

data class ChatMessage(
    val message: String,
    val isSent: Boolean,  // true if sent by user, false if received
    val time: String
)
