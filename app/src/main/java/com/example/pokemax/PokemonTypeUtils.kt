package com.example.pokemax

import androidx.compose.ui.graphics.Color
import com.example.pokemax.ui.theme.*

data class GenerationInfo(
    val id: Int,
    val name: String,
    val regionName: String,
    val startId: Int,
    val endId: Int
) {
    val limit: Int
        get() = endId - startId + 1

    val offset: Int
        get() = startId - 1
}

object PokemonGenerations {
    val ALL = listOf(
        GenerationInfo(1, "Gen I", "Kanto", 1, 151),
        GenerationInfo(2, "Gen II", "Johto", 152, 251),
        GenerationInfo(3, "Gen III", "Hoenn", 252, 386),
        GenerationInfo(4, "Gen IV", "Sinnoh", 387, 493),
        GenerationInfo(5, "Gen V", "Unova", 494, 649),
        GenerationInfo(6, "Gen VI", "Kalos", 650, 721),
        GenerationInfo(7, "Gen VII", "Alola", 722, 809),
        GenerationInfo(8, "Gen VIII", "Galar", 810, 898),
        GenerationInfo(9, "Gen IX", "Paldea", 899, 1025)
    )
}

fun getPokemonTypeColor(typeName: String?): Color {
    return when (typeName?.lowercase()) {
        "normal" -> TypeNormal
        "fire", "fogo" -> TypeFire
        "water", "água", "agua" -> TypeWater
        "grass", "planta" -> TypeGrass
        "electric", "elétrico", "eletrico" -> TypeElectric
        "ice", "gelo" -> TypeIce
        "fighting", "lutador" -> TypeFighting
        "poison", "veneno" -> TypePoison
        "ground", "terra" -> TypeGround
        "flying", "voador" -> TypeFlying
        "psychic", "psíquico", "psiquico" -> TypePsychic
        "bug", "inseto" -> TypeBug
        "rock", "pedra" -> TypeRock
        "ghost", "fantasma" -> TypeGhost
        "dragon", "dragão", "dragao" -> TypeDragon
        "dark", "noturno" -> TypeDark
        "steel", "aço", "aco" -> TypeSteel
        "fairy", "fada" -> TypeFairy
        else -> TypeNormal
    }
}

fun translatePokemonType(typeName: String?): String {
    return when (typeName?.lowercase()) {
        "normal" -> "NORMAL"
        "fire" -> "FOGO"
        "water" -> "ÁGUA"
        "grass" -> "PLANTA"
        "electric" -> "ELÉTRICO"
        "ice" -> "GELO"
        "fighting" -> "LUTADOR"
        "poison" -> "VENENO"
        "ground" -> "TERRA"
        "flying" -> "VOADOR"
        "psychic" -> "PSÍQUICO"
        "bug" -> "INSETO"
        "rock" -> "PEDRA"
        "ghost" -> "FANTASMA"
        "dragon" -> "DRAGÃO"
        "dark" -> "NOTURNO"
        "steel" -> "AÇO"
        "fairy" -> "FADA"
        else -> typeName?.uppercase() ?: "NORMAL"
    }
}

fun translateStatName(statName: String?): String {
    return when (statName?.lowercase()) {
        "hp" -> "HP"
        "attack" -> "ATQ"
        "defense" -> "DEF"
        "special-attack" -> "SP.ATQ"
        "special-defense" -> "SP.DEF"
        "speed" -> "VELOC"
        else -> statName?.uppercase() ?: ""
    }
}

fun translateDamageClass(damageClass: String?): String {
    return when (damageClass?.lowercase()) {
        "physical" -> "FÍSICO"
        "special" -> "ESPECIAL"
        "status" -> "SUPORTE"
        else -> damageClass?.uppercase() ?: "OUTRO"
    }
}
