package com.messenger.indiChat.repository

import com.messenger.indiChat.models.ChatMessage
import com.messenger.indiChat.models.User
import com.messenger.indiChat.network.ChatApi

class ChatRepository(private val api: ChatApi) {

    fun getMessages(user2: String): List<ChatMessage> {
        val response = api.getMessages(user2).execute()
        return response.body()?.data ?: emptyList()
    }

    suspend fun getAllUsers(): List<User> {
        val response = api.getAllUsers()
        return response.data ?: emptyList()
    }
}
