package com.example.testgpt.utils

import com.example.testgpt.model.ChatRequest
import com.example.testgpt.model.ChatRequestMessage
import com.example.testgpt.model.GrammarGroup
import com.example.testgpt.model.Phrase
import com.example.testgpt.network.OpenAiApi

/**
 * Генерация фраз с проверкой и повторной отправкой, если есть ошибка.
 */
object PhraseGenerator {

    /**
     * Генерирует фразы по грамматике и списку слов, с проверкой на корректность.
     */
    suspend fun generatePhrasesWithGpt(
        api: OpenAiApi,
        grammars: List<String>,
        words: List<String>,
        phraseCount: Int
    ): List<GrammarGroup> {
        val result = mutableListOf<GrammarGroup>()

        val allowedWords = words.map { it.lowercase() }.toSet() +
                setOf("is", "are", "am", "was", "were", "not", "a", "an", "the")

        fun isValidPhrase(text: String): Boolean {
            val tokens = text.lowercase()
                .replace(Regex("[^a-zA-Z ]"), "") // Удаляем знаки
                .split("\\s+".toRegex())
            return tokens.all { it in allowedWords }
        }

        for (grammar in grammars) {
            var phrases = listOf<Phrase>()
            var valid = false
            var attempts = 0

            while (!valid && attempts < 5) {
                attempts++

                val prompt = buildString {
                    append("Составь строго $phraseCount отдельных предложений на английском языке.\n")
                    append("Каждое предложение должно:\n")
                    append("• быть грамматически правильным\n")
                    append("• соответствовать грамматике: $grammar\n")
                    append("• содержать не более 7 слов\n")
                    append("• использовать ТОЛЬКО слова из списка: ${words.joinToString(", ")}\n")
                    append("• МОЖНО использовать: артикли (a, an, the), формы to be (is, are, was, were, am), not\n")
                    append("❌ ЗАПРЕЩЕНО использовать любые другие слова (включая местоимения, предлоги, союзы, if, will, do и т.п.), если они не входят в список\n\n")

                    append("Если ты НЕ можешь составить фразу строго по правилам — не пиши ничего.\n")
                    append("После генерации проверь КАЖДОЕ предложение:\n")
                    append("✔ Только слова из списка (и разрешённые)?\n")
                    append("✔ Не более 7 слов?\n")
                    append("✔ Верная грамматика: $grammar?\n")
                    append("Если нет — удали всё и начни заново.\n\n")

                    append("Пример:\n")
                    append("Список слов: television, partners, touch, effort\n")
                    append("Грамматика: Present Simple\n")
                    append("Ответ:\n")
                    append("1. The partners touch the television.\n   Партнёры трогают телевизор.\n")
                    append("2. The effort touches the partners.\n   Усилие касается партнёров.\n\n")

                    append("Выводи только результат в формате:\n")
                    append("1. [английское предложение]\n   [перевод]\n")
                    append("2. ...\n")
                    append("Выдай только результат — без комментариев.")
                }

                val request = ChatRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(ChatRequestMessage(content = prompt))
                )

                val response = api.generate(request)
                val content = response.choices.firstOrNull()?.message?.content ?: continue

                val parts = content.split(Regex("\\d+\\.")).map { it.trim() }.filter { it.isNotEmpty() }

                phrases = parts.mapNotNull {
                    val lines = it.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    if (lines.size >= 2 && isValidPhrase(lines[0])) {
                        Phrase(
                            sentence = lines[0],
                            translation = lines[1],
                            grammar = grammar
                        )
                    } else null
                }

                valid = phrases.size == phraseCount
            }

            if (phrases.size == phraseCount) {
                result.add(GrammarGroup(grammar, phrases))
            } else {
                val errorPhrase = Phrase(
                    sentence = "⚠ Не удалось сгенерировать предложения.",
                    translation = "GPT не справился после 5 попыток.",
                    grammar = grammar
                )
                result.add(GrammarGroup(grammar, listOf(errorPhrase)))
            }
        }

        return result
    }
}