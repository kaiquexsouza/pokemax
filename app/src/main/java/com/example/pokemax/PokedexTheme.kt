package com.example.pokemax

import androidx.compose.ui.graphics.Color
import com.example.pokemax.ui.theme.*

enum class PokedexThemeEnum(
    val id: String,
    val displayName: String,
    val frameColor: Color,
    val frameDarkColor: Color,
    val screenBgColor: Color,
    val trimColor: Color,
    val ledColor: Color,
    val cyanAccent: Color
) {
    RED_CLASSIC(
        id = "red_classic",
        displayName = "🔴 Red Classic",
        frameColor = PokedexRedFrame,
        frameDarkColor = PokedexRedDark,
        screenBgColor = PokedexBlueScreen,
        trimColor = PokedexSilverTrim,
        ledColor = Color(0xFF38BDF8),
        cyanAccent = PokedexCyanAccent
    ),
    GOLD_EDITION(
        id = "gold_edition",
        displayName = "🟡 Gold Edition",
        frameColor = Color(0xFFFFD700),
        frameDarkColor = Color(0xFFB8860B),
        screenBgColor = Color(0xFF1C1917),
        trimColor = Color(0xFFFEF08A),
        ledColor = Color(0xFFFACC15),
        cyanAccent = Color(0xFFFDE047)
    ),
    CYBER_DARK(
        id = "cyber_dark",
        displayName = "⬛ Cyber Dark",
        frameColor = Color(0xFF1E293B),
        frameDarkColor = Color(0xFF0F172A),
        screenBgColor = Color(0xFF020617),
        trimColor = Color(0xFF475569),
        ledColor = Color(0xFF06B6D4),
        cyanAccent = Color(0xFF22D3EE)
    ),
    EMERALD_RAYQUAZA(
        id = "emerald_rayquaza",
        displayName = "🟢 Emerald Rayquaza",
        frameColor = Color(0xFF059669),
        frameDarkColor = Color(0xFF047857),
        screenBgColor = Color(0xFF022C22),
        trimColor = Color(0xFFA7F3D0),
        ledColor = Color(0xFF10B981),
        cyanAccent = Color(0xFF34D399)
    )
}
