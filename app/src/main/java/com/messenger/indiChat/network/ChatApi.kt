package com.messenger.indiChat.network

import com.messenger.indiChat.models.ChatMessage
import com.messenger.indiChat.models.GenericResponse
import com.messenger.indiChat.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApi {

    @GET("api/chat/messages")
    fun getMessages(
        @Query("user2") user2: String
    ): Call<GenericResponse<List<ChatMessage>>>

    @GET("api/chat/users")
    suspend fun getChatUsers(): GenericResponse<List<User>>

    @GET("api/users/all")
    suspend fun getAllUsers(): GenericResponse<List<User>>
}
