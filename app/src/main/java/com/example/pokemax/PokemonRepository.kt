package com.example.pokemax

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import kotlinx.coroutines.awaitAll

class PokemonRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(PokeApiService::class.java)

    suspend fun fetchPokemonListForGen(gen: GenerationInfo, searchQuery: String = ""): List<Pokemon> = coroutineScope {
        val rawList = apiService.getPokemonList(limit = gen.limit, offset = gen.offset).results

        // Filter by searchQuery if provided
        val filtered = if (searchQuery.isBlank()) {
            rawList
        } else {
            val queryClean = searchQuery.trim().lowercase().removePrefix("#")
            rawList.filter { pokemon ->
                val nameMatch = pokemon.name.lowercase().contains(queryClean)
                val idMatch = pokemon.id.toString() == queryClean || pokemon.formattedId.lowercase().contains(queryClean)
                nameMatch || idMatch
            }
        }

        // Fetch details for filtered items to get type badges
        filtered.map { pokemon ->
            async {
                try {
                    val detail = apiService.getPokemonDetail(pokemon.id)
                    pokemon.copy(details = detail)
                } catch (_: Exception) {
                    pokemon
                }
            }
        }.awaitAll()
    }

    suspend fun fetchPokemonDetail(id: Int): PokemonDetail {
        return apiService.getPokemonDetail(id)
    }

    suspend fun fetchEvolutionChain(pokemonId: Int): ChainLink? {
        return try {
            val species = apiService.getPokemonSpecies(pokemonId)
            val chainUrl = species.evolutionChain?.url
            if (!chainUrl.isNullOrEmpty()) {
                val evolutionChain = apiService.getEvolutionChainByUrl(chainUrl)
                evolutionChain.chain
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchMoveDetail(moveNameOrId: String): MoveDetailResponse {
        return apiService.getMoveDetail(moveNameOrId)
    }
}
