package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class EnglishSpeechHelper(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
                if (!isReady) {
                    val ukResult = tts?.setLanguage(Locale.UK)
                    isReady = ukResult != TextToSpeech.LANG_MISSING_DATA &&
                        ukResult != TextToSpeech.LANG_NOT_SUPPORTED
                }
                tts?.setPitch(1.08f)
            } else {
                Log.w("SpeechHelper", "TextToSpeech initialization failed with status: $status")
            }
        }
    }

    fun speakEnglish(text: String, slow: Boolean = false) {
        val engine = tts ?: return
        if (!isReady) {
            engine.setLanguage(Locale.US)
        }
        engine.setSpeechRate(if (slow) 0.58f else 0.90f)
        engine.setPitch(1.08f)
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
