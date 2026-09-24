package com.example.pokemax

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetail

    @GET("pokemon/{name}")
    suspend fun getPokemonDetailByName(
        @Path("name") name: String
    ): PokemonDetail

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): PokemonSpeciesResponse

    @GET
    suspend fun getEvolutionChainByUrl(
        @Url url: String
    ): EvolutionChainResponse

    @GET("move/{nameOrId}")
    suspend fun getMoveDetail(
        @Path("nameOrId") nameOrId: String
    ): MoveDetailResponse
}
