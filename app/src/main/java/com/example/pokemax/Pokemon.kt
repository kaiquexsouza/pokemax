package com.example.pokemax

import com.google.gson.annotations.SerializedName

data class Pokemon(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    val details: PokemonDetail? = null
) {
    val id: Int
        get() {
            val segments = url.trimEnd('/').split("/")
            return segments.lastOrNull()?.toIntOrNull() ?: 0
        }

    val formattedId: String
        get() = "#$id"

    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}
