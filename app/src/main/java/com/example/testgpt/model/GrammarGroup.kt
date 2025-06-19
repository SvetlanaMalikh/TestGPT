package com.example.testgpt.model

/**
 * Группа фраз, объединённых одной грамматикой.
 */
data class GrammarGroup(
    val grammar: String,
    val phrases: List<Phrase>
)