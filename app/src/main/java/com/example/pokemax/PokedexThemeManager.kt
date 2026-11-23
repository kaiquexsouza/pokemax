package com.example.pokemax

import android.content.Context
import android.content.SharedPreferences

class PokedexThemeManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pokemax_theme_prefs", Context.MODE_PRIVATE)

    var savedTheme: PokedexThemeEnum
        get() {
            val themeId = prefs.getString("key_saved_theme", PokedexThemeEnum.RED_CLASSIC.id)
            return PokedexThemeEnum.entries.firstOrNull { it.id == themeId } ?: PokedexThemeEnum.RED_CLASSIC
        }
        set(value) {
            prefs.edit().putString("key_saved_theme", value.id).apply()
        }
}
