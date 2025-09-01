package com.messenger.indiChat.network

import com.messenger.indiChat.models.LoginRequest
import com.messenger.indiChat.models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login") // replace with your actual endpoint
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
