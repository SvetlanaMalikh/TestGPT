package com.example.testgpt.utils

import android.content.Context
import com.example.testgpt.model.Word
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DictionaryLoader {
    fun loadWords(context: Context, safeMode: Boolean = false): List<Word> {
        val fileName = if (safeMode) "words_safe.json" else "words.json"
        val inputStream = context.assets.open(fileName)
        val json = inputStream.bufferedReader().use { it.readText() }
        return Gson().fromJson(json, object : TypeToken<List<Word>>() {}.type)
    }
}