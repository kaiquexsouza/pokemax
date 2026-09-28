package com.example.pokemax

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PokemonRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(PokeApiService::class.java)

    private val detailCache = ConcurrentHashMap<Int, PokemonDetail>()
    private val chainCache = ConcurrentHashMap<Int, ChainLink>()
    private val moveCache = ConcurrentHashMap<String, MoveDetailResponse>()

    fun getInstantLocalList(gen: GenerationInfo?, searchQuery: String = ""): List<Pokemon> {
        val startId = gen?.startId ?: 1
        val endId = gen?.endId ?: 1025

        val localList = (startId..endId).map { id ->
            val cached = detailCache[id]
            Pokemon(
                name = cached?.name ?: getKnownPokemonName(id),
                url = "https://pokeapi.co/api/v2/pokemon/$id/",
                details = cached
            )
        }

        if (searchQuery.isBlank()) return localList

        val queryClean = searchQuery.trim().lowercase().removePrefix("#")
        return localList.filter { pokemon ->
            val nameMatch = pokemon.name.lowercase().contains(queryClean)
            val idMatch = pokemon.id.toString() == queryClean || pokemon.formattedId.lowercase().contains(queryClean)
            nameMatch || idMatch
        }
    }

    suspend fun fetchPokemonListFast(gen: GenerationInfo?, searchQuery: String = ""): List<Pokemon> {
        val limit = gen?.limit ?: 1025
        val offset = gen?.offset ?: 0

        val rawList = try {
            apiService.getPokemonList(limit = limit, offset = offset).results
        } catch (_: Exception) {
            getInstantLocalList(gen, searchQuery)
        }

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

        return filtered.map { pokemon ->
            val cachedDetail = detailCache[pokemon.id]
            if (cachedDetail != null) {
                pokemon.copy(details = cachedDetail)
            } else {
                pokemon
            }
        }
    }

    suspend fun enrichPokemonDetailsInBackground(
        currentList: List<Pokemon>,
        onBatchUpdated: (List<Pokemon>) -> Unit
    ) = coroutineScope {
        val unCached = currentList.filter { it.details == null }
        if (unCached.isEmpty()) return@coroutineScope

        val workingMap = currentList.associateBy { it.id }.toMutableMap()
        val batches = unCached.chunked(15)

        for ((index, batch) in batches.withIndex()) {
            val batchResults = batch.map { pokemon ->
                async {
                    try {
                        val detail = apiService.getPokemonDetail(pokemon.id)
                        detailCache[pokemon.id] = detail
                        pokemon.copy(details = detail)
                    } catch (_: Exception) {
                        pokemon
                    }
                }
            }.awaitAll()

            for (updated in batchResults) {
                workingMap[updated.id] = updated
            }

            onBatchUpdated(currentList.map { workingMap[it.id] ?: it })

            if (index < batches.size - 1) {
                delay(30)
            }
        }
    }

    suspend fun fetchPokemonDetail(id: Int): PokemonDetail {
        val cached = detailCache[id]
        if (cached != null) return cached

        val detail = apiService.getPokemonDetail(id)
        detailCache[id] = detail
        return detail
    }

    suspend fun fetchEvolutionChain(pokemonId: Int): ChainLink? {
        val cached = chainCache[pokemonId]
        if (cached != null) return cached

        return try {
            val species = apiService.getPokemonSpecies(pokemonId)
            val chainUrl = species.evolutionChain?.url
            if (!chainUrl.isNullOrEmpty()) {
                val evolutionChain = apiService.getEvolutionChainByUrl(chainUrl)
                val chain = evolutionChain.chain
                chainCache[pokemonId] = chain
                chain
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun fetchMoveDetail(moveNameOrId: String): MoveDetailResponse {
        val key = moveNameOrId.trim().lowercase()
        val cached = moveCache[key]
        if (cached != null) return cached

        val moveDetail = apiService.getMoveDetail(key)
        moveCache[key] = moveDetail
        return moveDetail
    }

    private fun getKnownPokemonName(id: Int): String {
        return when (id) {
            1 -> "bulbasaur"
            2 -> "ivysaur"
            3 -> "venusaur"
            4 -> "charmander"
            5 -> "charmeleon"
            6 -> "charizard"
            7 -> "squirtle"
            8 -> "wartortle"
            9 -> "blastoise"
            10 -> "caterpie"
            11 -> "metapod"
            12 -> "butterfree"
            13 -> "weedle"
            14 -> "kakuna"
            15 -> "beedrill"
            16 -> "pidgey"
            17 -> "pidgeotto"
            18 -> "pidgeot"
            19 -> "rattata"
            20 -> "raticate"
            25 -> "pikachu"
            26 -> "raichu"
            133 -> "eevee"
            134 -> "vaporeon"
            135 -> "jolteon"
            136 -> "flareon"
            150 -> "mewtwo"
            151 -> "mew"
            else -> "pokemon-$id"
        }
    }
}
