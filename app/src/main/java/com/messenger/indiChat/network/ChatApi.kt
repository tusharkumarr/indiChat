package com.messenger.indiChat.network

import com.messenger.indiChat.models.ChatMessage
import com.messenger.indiChat.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApi {

    // Fetch all previous messages between current user and another user
    @GET("api/chat/messages")
    fun getMessages(
        @Query("user2") user2: String
    ): Call<List<ChatMessage>>

    // Fetch users that the current user has chatted with
    @GET("api/chat/users")
    suspend fun getChatUsers(): List<User>

    // Fetch all users (for starting a new chat)
    @GET("api/users/all")
    suspend fun getAllUsers(): List<User>
}
