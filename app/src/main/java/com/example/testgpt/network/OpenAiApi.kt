package com.example.testgpt.network

import com.example.testgpt.model.ChatRequest
import com.example.testgpt.model.ChatResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiApi {

    @Headers(
        "Content-Type: application/json",
        "HTTP-Referer: https://example.com",
        "X-Title: GrammarApp"
    )

    @POST("v1/chat/completions")
    suspend fun generate(@Body request: ChatRequest): ChatResponse

    @POST("v1/chat/completions")
    suspend fun generateRaw(@Body request: ChatRequest): ResponseBody
}