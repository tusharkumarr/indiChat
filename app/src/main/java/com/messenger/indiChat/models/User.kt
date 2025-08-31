package com.messenger.indiChat.models

data class User(
    val id: String,
    val name: String,
    val lastMessage: String? = null, // optional
    val lastMessageTime: String? = null
)
