package com.example.pokemax

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

fun getPokemonTypeBrush(types: List<PokemonTypeSlot>?): Brush {
    if (types.isNullOrEmpty()) {
        return SolidColor(TypeNormal)
    }
    if (types.size == 1) {
        return SolidColor(getPokemonTypeColor(types[0].type.name))
    }
    val color1 = getPokemonTypeColor(types[0].type.name)
    val color2 = getPokemonTypeColor(types[1].type.name)
    return Brush.horizontalGradient(
        colors = listOf(color1, color2)
    )
}

data class TypeEffectivenessResult(
    val quadWeaknesses: List<String>,   // 4x
    val doubleWeaknesses: List<String>, // 2x
    val doubleResistances: List<String>,// 0.5x
    val quadResistances: List<String>,  // 0.25x
    val immunities: List<String>        // 0x
)

fun calculateTypeEffectiveness(types: List<PokemonTypeSlot>?): TypeEffectivenessResult {
    val allTypes = listOf(
        "normal", "fire", "water", "grass", "electric", "ice",
        "fighting", "poison", "ground", "flying", "psychic", "bug",
        "rock", "ghost", "dragon", "dark", "steel", "fairy"
    )
    val defenderTypes = types?.map { it.type.name.lowercase() } ?: listOf("normal")

    val multipliers = mutableMapOf<String, Double>()
    for (atk in allTypes) {
        var mult = 1.0
        for (def in defenderTypes) {
            mult *= getSingleTypeEffectiveness(atk, def)
        }
        multipliers[atk] = mult
    }

    return TypeEffectivenessResult(
        quadWeaknesses = multipliers.filter { it.value >= 3.9 }.keys.toList(),
        doubleWeaknesses = multipliers.filter { it.value in 1.9..2.1 }.keys.toList(),
        doubleResistances = multipliers.filter { it.value in 0.4..0.6 }.keys.toList(),
        quadResistances = multipliers.filter { it.value in 0.2..0.3 }.keys.toList(),
        immunities = multipliers.filter { it.value == 0.0 }.keys.toList()
    )
}

private fun getSingleTypeEffectiveness(attacker: String, defender: String): Double {
    return when (attacker) {
        "normal" -> when (defender) {
            "rock", "steel" -> 0.5
            "ghost" -> 0.0
            else -> 1.0
        }
        "fire" -> when (defender) {
            "fire", "water", "rock", "dragon" -> 0.5
            "grass", "ice", "bug", "steel" -> 2.0
            else -> 1.0
        }
        "water" -> when (defender) {
            "water", "grass", "dragon" -> 0.5
            "fire", "ground", "rock" -> 2.0
            else -> 1.0
        }
        "grass" -> when (defender) {
            "fire", "grass", "poison", "flying", "bug", "dragon", "steel" -> 0.5
            "water", "ground", "rock" -> 2.0
            else -> 1.0
        }
        "electric" -> when (defender) {
            "electric", "grass", "dragon" -> 0.5
            "water", "flying" -> 2.0
            "ground" -> 0.0
            else -> 1.0
        }
        "ice" -> when (defender) {
            "fire", "water", "ice", "steel" -> 0.5
            "grass", "ground", "flying", "dragon" -> 2.0
            else -> 1.0
        }
        "fighting" -> when (defender) {
            "poison", "flying", "psychic", "bug", "fairy" -> 0.5
            "normal", "ice", "rock", "dark", "steel" -> 2.0
            "ghost" -> 0.0
            else -> 1.0
        }
        "poison" -> when (defender) {
            "poison", "ground", "rock", "ghost" -> 0.5
            "grass", "fairy" -> 2.0
            "steel" -> 0.0
            else -> 1.0
        }
        "ground" -> when (defender) {
            "grass", "bug" -> 0.5
            "fire", "electric", "poison", "rock", "steel" -> 2.0
            "flying" -> 0.0
            else -> 1.0
        }
        "flying" -> when (defender) {
            "electric", "rock", "steel" -> 0.5
            "grass", "fighting", "bug" -> 2.0
            else -> 1.0
        }
        "psychic" -> when (defender) {
            "psychic", "steel" -> 0.5
            "fighting", "poison" -> 2.0
            "dark" -> 0.0
            else -> 1.0
        }
        "bug" -> when (defender) {
            "fire", "fighting", "poison", "flying", "ghost", "steel", "fairy" -> 0.5
            "grass", "psychic", "dark" -> 2.0
            else -> 1.0
        }
        "rock" -> when (defender) {
            "fighting", "ground", "steel" -> 0.5
            "fire", "ice", "flying", "bug" -> 2.0
            else -> 1.0
        }
        "ghost" -> when (defender) {
            "dark" -> 0.5
            "psychic", "ghost" -> 2.0
            "normal" -> 0.0
            else -> 1.0
        }
        "dragon" -> when (defender) {
            "steel" -> 0.5
            "dragon" -> 2.0
            "fairy" -> 0.0
            else -> 1.0
        }
        "dark" -> when (defender) {
            "fighting", "dark", "fairy" -> 0.5
            "psychic", "ghost" -> 2.0
            else -> 1.0
        }
        "steel" -> when (defender) {
            "fire", "water", "electric", "steel" -> 0.5
            "ice", "rock", "fairy" -> 2.0
            else -> 1.0
        }
        "fairy" -> when (defender) {
            "fire", "poison", "steel" -> 0.5
            "fighting", "dragon", "dark" -> 2.0
            else -> 1.0
        }
        else -> 1.0
    }
}
