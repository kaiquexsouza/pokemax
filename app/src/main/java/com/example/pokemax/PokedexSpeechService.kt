package com.example.pokemax

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class PokedexSpeechService(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ptBr = Locale.forLanguageTag("pt-BR")
            val result = tts?.setLanguage(ptBr)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.forLanguageTag("pt"))
            }
            isInitialized = true
        } else {
            Log.e("PokedexSpeechService", "TextToSpeech init failed with status $status")
        }
    }

    fun speak(text: String) {
        if (isInitialized && text.isNotBlank()) {
            tts?.stop()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PokedexUtteranceId")
        }
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    fun isSpeaking(): Boolean {
        return tts?.isSpeaking == true
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("PokedexSpeechService", "Error shutting down TTS", e)
        } finally {
            tts = null
            isInitialized = false
        }
    }
}
