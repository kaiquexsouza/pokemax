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
    @SerializedName("evolution_chain") val evolutionChain: UrlResource?
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

    /**
     * Retorna se a cadeia evolutiva possui mais de 1 estágio (ou seja, se há evolução).
     */
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
    val fromSpeciesId: Int,
    val toSpeciesName: String,
    val toSpeciesId: Int,
    val requirement: String
)

/**
 * Função utilitária para extrair os passos evolutivos de uma cadeia.
 */
fun extractEvolutionSteps(rootLink: ChainLink): List<EvolutionStep> {
    val steps = mutableListOf<EvolutionStep>()

    fun traverse(parent: ChainLink) {
        for (child in parent.evolvesTo) {
            val req = child.evolutionDetails.firstOrNull()?.formattedRequirement ?: "Evolução"
            steps.add(
                EvolutionStep(
                    fromSpeciesName = parent.species.name,
                    fromSpeciesId = parent.speciesId,
                    toSpeciesName = child.species.name,
                    toSpeciesId = child.speciesId,
                    requirement = req
                )
            )
            traverse(child)
        }
    }

    traverse(rootLink)
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
    @SerializedName("learned_by_pokemon") val learnedByPokemon: List<NamedApiResource>?
) {
    val description: String
        get() {
            val ptText = flavorTextEntries?.firstOrNull { it.language.name == "pt-BR" || it.language.name == "pt" }?.flavorText
            val enText = flavorTextEntries?.firstOrNull { it.language.name == "en" }?.flavorText
            return (ptText ?: enText ?: "Sem descrição disponível.").replace("\n", " ")
        }

    val effectText: String?
        get() {
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
