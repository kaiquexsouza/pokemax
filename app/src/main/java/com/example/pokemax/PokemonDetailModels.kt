package com.example.pokemax

import com.google.gson.annotations.SerializedName

data class NamedApiResource(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
)

data class PokemonTypeSlot(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val type: NamedApiResource
)

data class PokemonAbilitySlot(
    @SerializedName("is_hidden") val isHidden: Boolean,
    @SerializedName("slot") val slot: Int,
    @SerializedName("ability") val ability: NamedApiResource
)

data class PokemonMoveSlot(
    @SerializedName("move") val move: NamedApiResource
)

data class UrlResource(
    @SerializedName("url") val url: String
)

data class PokemonSpeciesResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("evolution_chain") val evolutionChain: UrlResource?,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>? = null
)

data class EvolutionChainResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("chain") val chain: ChainLink
)

data class ChainLink(
    @SerializedName("species") val species: NamedApiResource,
    @SerializedName("evolution_details") val evolutionDetails: List<EvolutionDetail>,
    @SerializedName("evolves_to") val evolvesTo: List<ChainLink>
) {
    val speciesId: Int
        get() {
            val segments = species.url.trimEnd('/').split("/")
            return segments.lastOrNull()?.toIntOrNull() ?: 0
        }

    fun hasEvolutions(): Boolean {
        return evolvesTo.isNotEmpty()
    }
}

data class EvolutionDetail(
    @SerializedName("min_level") val minLevel: Int?,
    @SerializedName("item") val item: NamedApiResource?,
    @SerializedName("trigger") val trigger: NamedApiResource?,
    @SerializedName("held_item") val heldItem: NamedApiResource?,
    @SerializedName("known_move") val knownMove: NamedApiResource?,
    @SerializedName("time_of_day") val timeOfDay: String?
) {
    val formattedRequirement: String
        get() {
            return when {
                item != null -> "Item: ${item.name.replace('-', ' ').replaceFirstChar { it.uppercase() }}"
                minLevel != null -> "Nível $minLevel"
                trigger != null -> trigger.name.replace('-', ' ').replaceFirstChar { it.uppercase() }
                else -> "Evolução"
            }
        }
}

data class EvolutionStep(
    val fromSpeciesName: String,
    val fromSpeciesIdOrName: String,
    val fromSpeciesId: Int,
    val toSpeciesName: String,
    val toSpeciesIdOrName: String,
    val toSpeciesId: Int,
    val requirement: String,
    val isMega: Boolean = false
)

fun extractEvolutionSteps(rootLink: ChainLink): List<EvolutionStep> {
    val steps = mutableListOf<EvolutionStep>()
    val visitedSpeciesIds = mutableSetOf<Int>()

    fun traverse(parent: ChainLink) {
        visitedSpeciesIds.add(parent.speciesId)
        for (child in parent.evolvesTo) {
            visitedSpeciesIds.add(child.speciesId)
            val req = child.evolutionDetails.firstOrNull()?.formattedRequirement ?: "Evolução"
            steps.add(
                EvolutionStep(
                    fromSpeciesName = parent.species.name,
                    fromSpeciesIdOrName = parent.speciesId.toString(),
                    fromSpeciesId = parent.speciesId,
                    toSpeciesName = child.species.name,
                    toSpeciesIdOrName = child.speciesId.toString(),
                    toSpeciesId = child.speciesId,
                    requirement = req
                )
            )
            traverse(child)
        }
    }

    traverse(rootLink)

    for (speciesId in visitedSpeciesIds) {
        val megas = getMegaEvolutionsForPokemon(speciesId)
        for (mega in megas) {
            steps.add(
                EvolutionStep(
                    fromSpeciesName = mega.basePokemonName,
                    fromSpeciesIdOrName = mega.basePokemonId.toString(),
                    fromSpeciesId = mega.basePokemonId,
                    toSpeciesName = mega.megaFormDisplayName,
                    toSpeciesIdOrName = mega.megaPokemonName,
                    toSpeciesId = mega.megaPokemonId,
                    requirement = "Item: ${mega.megaStoneName}",
                    isMega = true
                )
            )
        }
    }

    return steps
}

data class MoveDetailResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("power") val power: Int?,
    @SerializedName("accuracy") val accuracy: Int?,
    @SerializedName("pp") val pp: Int?,
    @SerializedName("type") val type: NamedApiResource,
    @SerializedName("damage_class") val damageClass: NamedApiResource?,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>?,
    @SerializedName("effect_entries") val effectEntries: List<EffectEntry>?,
    @SerializedName("effect_chance") val effectChance: Int?,
    @SerializedName("learned_by_pokemon") val learnedByPokemon: List<NamedApiResource>?,
    var translatedDescription: String? = null,
    var translatedEffectText: String? = null
) {
    val description: String
        get() {
            if (!translatedDescription.isNullOrBlank()) return translatedDescription!!
            val ptText = flavorTextEntries?.firstOrNull { it.language.name == "pt-BR" || it.language.name == "pt" }?.flavorText
            val enText = flavorTextEntries?.firstOrNull { it.language.name == "en" }?.flavorText
            return (ptText ?: enText ?: "Sem descrição disponível.").replace("\n", " ").replace("\u000c", " ").trim()
        }

    val effectText: String?
        get() {
            if (!translatedEffectText.isNullOrBlank()) return translatedEffectText!!
            val effect = effectEntries?.firstOrNull { it.language.name == "en" }?.shortEffect
            if (effect != null && effectChance != null) {
                return effect.replace("\$effect_chance%", "$effectChance%")
            }
            return effect
        }
}

data class FlavorTextEntry(
    @SerializedName("flavor_text") val flavorText: String,
    @SerializedName("language") val language: NamedApiResource
)

data class EffectEntry(
    @SerializedName("effect") val effect: String,
    @SerializedName("short_effect") val shortEffect: String,
    @SerializedName("language") val language: NamedApiResource
)
