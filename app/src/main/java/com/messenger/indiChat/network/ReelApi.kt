package com.messenger.indiChat.network

import com.messenger.indiChat.models.GenericResponse
import com.messenger.indiChat.models.Reel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Response
import retrofit2.http.Query

interface ReelApi {
    @GET("api/reels")
    suspend fun getReels(): GenericResponse<List<Reel>>

    @Multipart
    @POST("api/reels/upload")
    suspend fun uploadReel(
        @Part file: MultipartBody.Part,
        @Part("caption") caption: RequestBody,
        @Part("userId") userId: RequestBody
    ): Response<ResponseBody>

    @GET("api/recommend/hybrid")
    suspend fun getHybridRecommendations(
        @Query("userId") userId: String,
        @Query("topN") topN: Int
    ): Response<GenericResponse<List<Reel>>>
}
