package com.example.pokemax

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val PokemonYellow = Color(0xFFFFCC00)
val PokemonBlueDark = Color(0xFF1E3A8A)
val PokemonBlueBorder = Color(0xFF2A75BB)

@Composable
fun PokemaxLogoText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 44.sp,
    shadowOffset: Dp = 3.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Darkest 3D Base Shadow
        Text(
            text = "Pokémax",
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = PokemonBlueDark,
            letterSpacing = 2.sp,
            modifier = Modifier.offset(x = shadowOffset, y = shadowOffset)
        )

        // Layer 2: Mid Blue 3D Outline
        Text(
            text = "Pokémax",
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = PokemonBlueBorder,
            letterSpacing = 2.sp,
            modifier = Modifier.offset(x = shadowOffset / 2, y = shadowOffset / 2)
        )

        // Layer 3: Top Yellow Fill (Pokémon Official Yellow)
        Text(
            text = "Pokémax",
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = PokemonYellow,
            letterSpacing = 2.sp
        )
    }
}
