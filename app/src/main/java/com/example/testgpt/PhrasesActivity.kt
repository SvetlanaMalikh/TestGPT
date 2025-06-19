package com.example.testgpt

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.testgpt.viewmodel.PhrasesViewModel
import com.example.testgpt.network.ApiClient
import com.example.testgpt.utils.DictionaryLoader
import kotlinx.coroutines.launch

class PhrasesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phrases)

        val viewModel: PhrasesViewModel by viewModels {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PhrasesViewModel(ApiClient.api) as T
                }
            }
        }

        val grammars = intent.getStringArrayListExtra("grammars") ?: listOf()
        val wordCount = intent.getIntExtra("wordCount", 3)
        val phraseCount = intent.getIntExtra("phraseCount", 1)
        val useSafeWords = intent.getBooleanExtra("useSafeWords", false)

        val phrasesLayout = findViewById<LinearLayout>(R.id.phrasesLayout)

        val selectedWords = DictionaryLoader.loadWords(this, safeMode = useSafeWords)
            .shuffled().take(wordCount).map { it.word }

        val countText = TextView(this).apply {
            text = "Слов выбрано: ${selectedWords.size}"
            textSize = 14f
            setPadding(0, 0, 0, 16)
        }
        phrasesLayout.addView(countText)

        val table = TableLayout(this).apply { setPadding(0, 0, 0, 32) }
        val chunked = selectedWords.chunked(4)
        chunked.forEach { rowWords ->
            val row = TableRow(this)
            rowWords.forEach { word ->
                val cell = TextView(this).apply {
                    text = word
                    textSize = 16f
                    setPadding(8, 8, 8, 8)
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                }
                row.addView(cell)
            }
            repeat(4 - rowWords.size) {
                row.addView(TextView(this).apply {
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                })
            }
            table.addView(row)
        }
        phrasesLayout.addView(table)

        viewModel.loadPhrases(grammars, selectedWords, phraseCount)

        lifecycleScope.launch {
            viewModel.phrases.collect { groups ->
                for (group in groups) {
                    val title = TextView(this@PhrasesActivity).apply {
                        text = group.grammar
                        textSize = 18f
                        setPadding(0, 32, 0, 8)
                    }
                    phrasesLayout.addView(title)

                    for (phrase in group.phrases) {
                        val sentence = TextView(this@PhrasesActivity).apply {
                            text = phrase.sentence
                            textSize = 16f
                        }
                        val translation = TextView(this@PhrasesActivity).apply {
                            text = phrase.translation
                        }
                        phrasesLayout.addView(sentence)
                        phrasesLayout.addView(translation)
                    }
                }
            }
        }
    }
}
