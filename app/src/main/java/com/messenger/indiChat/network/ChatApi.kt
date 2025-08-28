package com.messenger.indiChat.network


import com.messenger.indiChat.models.ChatMessage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApi {
    // Fetch all previous messages between two users
    @GET("api/chat/messages")
    fun getMessages(
        @Query("user1") user1: String,
        @Query("user2") user2: String
    ): Call<List<ChatMessage>>
}

