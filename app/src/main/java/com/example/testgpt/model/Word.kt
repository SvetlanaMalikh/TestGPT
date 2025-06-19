package com.example.testgpt.model

import com.google.gson.annotations.SerializedName

/**
 * Модель одного слова из словаря (JSON).
 * Аннотации позволяют связать поля с ключами в JSON.
 */
data class Word(
    @SerializedName("English")
    val word: String,

    @SerializedName("Russian")
    val translation: String,

    @SerializedName("Part of Speech")
    val partOfSpeech: String
)