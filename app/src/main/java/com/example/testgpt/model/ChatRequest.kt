package com.example.testgpt.model

/**
 * Модель запроса к OpenAI Chat API.
 */
data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatRequestMessage>,
    val temperature: Double = 0.7
)

data class ChatRequestMessage(
    val role: String = "user",
    val content: String
)