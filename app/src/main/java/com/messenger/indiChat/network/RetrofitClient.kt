package com.messenger.indiChat.network

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.gson.Gson
import com.messenger.indiChat.Activity.LoginActivity
import com.messenger.indiChat.models.ConstantValues
import com.messenger.indiChat.models.GenericResponse
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

            val errorInterceptor = Interceptor { chain ->
                val response = chain.proceed(chain.request())
                if (!response.isSuccessful) {
                    var message = "Error ${response.code}: ${response.message}"

                    try {
                        // Parse backend response
                        val errorBody = response.peekBody(Long.MAX_VALUE).string()
                        val parsed = Gson().fromJson(errorBody, GenericResponse::class.java)
                        if (parsed?.message != null) {
                            message = parsed.message

                            // 👇 Handle invalid/expired token specifically
                            if (message.contains("expired token", ignoreCase = true)) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Session expired. Please login again.", Toast.LENGTH_LONG).show()

                                    // Clear saved token
                                    val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                    sharedPref.edit().remove("jwtToken").apply()

                                    // Redirect to LoginActivity
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // Fallback toast for other errors
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
                response
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(authInterceptor)
                .addInterceptor(errorInterceptor)
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
    fun reelApi(context: Context) = getRetrofit(context).create(ReelApi::class.java)
}
