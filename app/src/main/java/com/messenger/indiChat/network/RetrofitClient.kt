package com.messenger.indiChat.network

import android.content.Context
import com.messenger.indiChat.models.ConstantValues
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private var retrofit: Retrofit? = null

    fun getRetrofit(context: Context): Retrofit {
        if (retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val authInterceptor = Interceptor { chain ->
                val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val token = sharedPref.getString("jwtToken", null)

                val requestBuilder = chain.request().newBuilder()
                token?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                chain.proceed(requestBuilder.build())
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(authInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(ConstantValues.RETROFIT_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun authApi(context: Context) = getRetrofit(context).create(AuthApi::class.java)
    fun chatApi(context: Context) = getRetrofit(context).create(ChatApi::class.java)
}
