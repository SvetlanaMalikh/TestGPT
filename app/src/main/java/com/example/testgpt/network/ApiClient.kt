package com.example.testgpt.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.Interceptor
import android.util.Log

private const val BASE_URL = "https://openrouter.ai/api/"
private const val API_KEY = "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" // 🔑 ВСТАВЬ СВОЙ КЛЮЧ

// 🔐 1. Интерцептор добавляет Authorization
val authInterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $API_KEY")
        .build()

    val response = chain.proceed(request)

    val body = response.body
    val content = body?.string()
    Log.e("GPT_HTTP", "HTTP ${response.code} — ответ: $content")

    val mediaType = body?.contentType()
    val newBody = content?.toResponseBody(mediaType)

    response.newBuilder().body(newBody).build()
}

// ⚙️ 2. Создаём client с интерцептором
private val client = OkHttpClient.Builder()
    .addInterceptor(authInterceptor)
    .build()

// 🔧 3. Подключаем client в Retrofit
object ApiClient {
    val api: OpenAiApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client) // ← вот ЭТО и есть .client(client)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .build()
        .create(OpenAiApi::class.java)
}