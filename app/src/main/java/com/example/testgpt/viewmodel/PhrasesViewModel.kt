package com.example.testgpt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testgpt.model.GrammarGroup
import com.example.testgpt.network.OpenAiApi
import com.example.testgpt.utils.PhraseGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для работы с фразами.
 */
class PhrasesViewModel(private val api: OpenAiApi) : ViewModel() {

    // Хранилище фраз
    private val _phrases = MutableStateFlow<List<GrammarGroup>>(emptyList())
    val phrases: StateFlow<List<GrammarGroup>> = _phrases

    /**
     * Загружает фразы с помощью GPT по грамматикам и словам.
     */
    fun loadPhrases(grammarList: List<String>, wordList: List<String>, phraseCount: Int) {
        viewModelScope.launch {
            val result = PhraseGenerator.generatePhrasesWithGpt(api, grammarList, wordList, phraseCount)
            _phrases.value = result
        }
    }
}