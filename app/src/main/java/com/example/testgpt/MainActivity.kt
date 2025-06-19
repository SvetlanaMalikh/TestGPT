package com.example.testgpt

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grammarGroup = findViewById<LinearLayout>(R.id.grammarGroup)
        val wordCountInput = findViewById<EditText>(R.id.wordCountInput)
        val phraseCountInput = findViewById<EditText>(R.id.phraseCountInput)
        val generateButton = findViewById<Button>(R.id.generateButton)
        val switchSafeMode = findViewById<SwitchCompat>(R.id.switchSafeMode)

        // ✅ Генерируем список грамматик
        val grammars = listOf(
            "Present Simple",
            "Past Simple",
            "Future Simple",
            "Present Continuous",
            "Conditional 1"
        )

        for (grammar in grammars) {
            val checkBox = CheckBox(this).apply {
                text = grammar
            }
            grammarGroup.addView(checkBox)
        }

        generateButton.setOnClickListener {
            val selectedGrammars = mutableListOf<String>()
            for (i in 0 until grammarGroup.childCount) {
                val view = grammarGroup.getChildAt(i)
                if (view is CheckBox && view.isChecked) {
                    selectedGrammars.add(view.text.toString())
                }
            }

            val wordCount = wordCountInput.text.toString().toIntOrNull() ?: 3
            val phraseCount = phraseCountInput.text.toString().toIntOrNull() ?: 1
            val useSafeWords = switchSafeMode.isChecked

            val intent = Intent(this, PhrasesActivity::class.java)
            intent.putStringArrayListExtra("grammars", ArrayList(selectedGrammars))
            intent.putExtra("wordCount", wordCount)
            intent.putExtra("phraseCount", phraseCount)
            intent.putExtra("useSafeWords", useSafeWords)
            startActivity(intent)
        }
    }
}