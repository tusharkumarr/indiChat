package com.messenger.indiChat.network

import com.messenger.indiChat.models.ConstantValues
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            // Logging interceptor
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(ConstantValues.RETROFIT_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    // Auth API instance
    val authApi: AuthApi by lazy {
        getRetrofit().create(AuthApi::class.java)
    }
    // Auth API instance
    val chatApi: ChatApi by lazy {
        getRetrofit().create(ChatApi::class.java)
    }
}
