package com.messenger.indiChat.repository

import com.messenger.indiChat.models.GenericResponse
import com.messenger.indiChat.models.Reel
import com.messenger.indiChat.network.ReelApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class ReelRepository(private val reelApi: ReelApi) {

    suspend fun uploadReel(
        file: MultipartBody.Part,
        caption: RequestBody,
        userId: RequestBody
    ): Response<ResponseBody> {
        return reelApi.uploadReel(file, caption, userId)
    }

    suspend fun getHybridRecommendations(
        userId: String,
        topN: Int
    ): Response<GenericResponse<List<Reel>>> {
        return reelApi.getHybridRecommendations(userId, topN)
    }
}
