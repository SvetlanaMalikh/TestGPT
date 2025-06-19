package com.example.testgpt.model

/**
 * Модель одной фразы, сгенерированной по грамматике.
 */
data class Phrase(
    val sentence: String,
    val translation: String,
    val grammar: String
)