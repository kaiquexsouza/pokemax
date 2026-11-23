package com.example.pokemax

import android.content.Context
import android.content.SharedPreferences

class QuizScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pokemax_quiz_prefs", Context.MODE_PRIVATE)

    var highScore: Int
        get() = prefs.getInt("key_high_score", 0)
        private set(value) = prefs.edit().putInt("key_high_score", value).apply()

    var maxStreak: Int
        get() = prefs.getInt("key_max_streak", 0)
        private set(value) = prefs.edit().putInt("key_max_streak", value).apply()

    fun updateScore(currentScore: Int, currentStreak: Int): Boolean {
        var isNewRecord = false
        if (currentScore > highScore) {
            highScore = currentScore
            isNewRecord = true
        }
        if (currentStreak > maxStreak) {
            maxStreak = currentStreak
        }
        return isNewRecord
    }
}
