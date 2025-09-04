package com.messenger.indiChat.repository

import com.messenger.indiChat.models.LoginRequest
import com.messenger.indiChat.models.LoginResponse
import com.messenger.indiChat.models.SignupRequest
import com.messenger.indiChat.models.SignupResponse
import com.messenger.indiChat.network.AuthApi

class AuthRepository(private val api: AuthApi) {

    suspend fun login(request: LoginRequest): LoginResponse? {
        val response = api.login(request)
        return if (response.success) response.data else null
    }

    suspend fun signup(request: SignupRequest): SignupResponse? {
        val response = api.signup(request)
        return if (response.success) response.data else null
    }
}
