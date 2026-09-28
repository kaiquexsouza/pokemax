package com.example.pokemax

import com.google.gson.annotations.SerializedName
import java.util.Locale

data class PokemonResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<Pokemon>
)

data class PokemonDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("weight") val weight: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("stats") val stats: List<StatSlot>,
    @SerializedName("types") val types: List<PokemonTypeSlot> = emptyList(),
    @SerializedName("abilities") val abilities: List<PokemonAbilitySlot> = emptyList(),
    @SerializedName("moves") val moves: List<PokemonMoveSlot> = emptyList(),
    @SerializedName("species") val species: NamedApiResource? = null
) {
    val animatedGifUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/$id.gif"

    val fallbackImageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

    val imageUrl: String
        get() = animatedGifUrl

    val formattedId: String
        get() = "#$id"

    val formattedWeight: String
        get() {
            val kg = weight / 10.0
            return String.format(Locale.US, "%.1fkg", kg).replace('.', ',')
        }

    val formattedHeight: String
        get() {
            val meters = height / 10.0
            return String.format(Locale.US, "%.1fm", meters).replace('.', ',')
        }

    val primaryType: String
        get() = types.firstOrNull()?.type?.name ?: "normal"
}

data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    @SerializedName("effort") val effort: Int,
    @SerializedName("stat") val stat: StatInfo
)

data class StatInfo(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)
