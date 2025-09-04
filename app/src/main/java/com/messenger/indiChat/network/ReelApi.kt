package com.messenger.indiChat.network

import com.messenger.indiChat.models.GenericResponse
import com.messenger.indiChat.models.Reel
import retrofit2.http.GET

interface ReelApi {
    @GET("api/reels")
    suspend fun getReels(): GenericResponse<List<Reel>>
}
