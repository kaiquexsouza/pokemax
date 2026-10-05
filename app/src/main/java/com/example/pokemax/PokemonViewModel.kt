package com.example.pokemax

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class DetailTab(val title: String) {
    STATS("STATS"),
    MOVES("MOVES"),
    EVOL("EVOL")
}

data class PokemonDetailData(
    val detail: PokemonDetail,
    val mainAbilities: List<String>,
    val hiddenAbilities: List<String>,
    val damageMoves: List<PokemonMoveSlot>,
    val supportMoves: List<PokemonMoveSlot>,
    val evolutionSteps: List<EvolutionStep>,
    val pokedexDescriptions: List<String>,
    val hasEvolution: Boolean
)

sealed class PokemonUiState {
    object Loading : PokemonUiState()
    data class Success(
        val pokemonList: List<Pokemon>,
        val activeGen: GenerationInfo?,
        val query: String
    ) : PokemonUiState()
    data class Error(val message: String) : PokemonUiState()
}

sealed class PokemonDetailUiState {
    object Idle : PokemonDetailUiState()
    object Loading : PokemonDetailUiState()
    data class Success(
        val data: PokemonDetailData,
        val selectedTab: DetailTab = DetailTab.STATS
    ) : PokemonDetailUiState()
    data class Error(val message: String) : PokemonDetailUiState()
}

sealed class MoveDetailUiState {
    object Idle : MoveDetailUiState()
    object Loading : MoveDetailUiState()
    data class Success(val moveDetail: MoveDetailResponse) : MoveDetailUiState()
    data class Error(val message: String) : MoveDetailUiState()
}

class PokemonViewModel(
    private val repository: PokemonRepository = PokemonRepository()
) : ViewModel() {

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _selectedGen = mutableStateOf<GenerationInfo?>(PokemonGenerations.ALL.first())
    val selectedGen: State<GenerationInfo?> = _selectedGen

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _isShiny = mutableStateOf(false)
    val isShiny: State<Boolean> = _isShiny

    private val _uiState = mutableStateOf<PokemonUiState>(PokemonUiState.Loading)
    val uiState: State<PokemonUiState> = _uiState

    private val _detailUiState = mutableStateOf<PokemonDetailUiState>(PokemonDetailUiState.Idle)
    val detailUiState: State<PokemonDetailUiState> = _detailUiState

    private val _moveDetailUiState = mutableStateOf<MoveDetailUiState>(MoveDetailUiState.Idle)
    val moveDetailUiState: State<MoveDetailUiState> = _moveDetailUiState

    private var listJob: Job? = null
    private var detailJob: Job? = null

    init {
        loadPokemonList()
    }

    fun performLogin(usernameOrEmail: String, password: String) {
        // Futura validação com banco de dados
        _isLoggedIn.value = true
    }

    fun loginAsGuest() {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun toggleShiny() {
        _isShiny.value = !_isShiny.value
    }

    fun onGenerationSelected(gen: GenerationInfo) {
        if (_selectedGen.value?.id == gen.id) {
            _selectedGen.value = null
        } else {
            _selectedGen.value = gen
        }
        loadPokemonList()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        listJob?.cancel()
        listJob = viewModelScope.launch {
            delay(300)
            loadPokemonList()
        }
    }

    fun loadPokemonList() {
        listJob?.cancel()
        listJob = viewModelScope.launch {
            val instantList = repository.getInstantLocalList(_selectedGen.value, _searchQuery.value)
            if (instantList.isNotEmpty()) {
                _uiState.value = PokemonUiState.Success(
                    pokemonList = instantList,
                    activeGen = _selectedGen.value,
                    query = _searchQuery.value
                )
            } else {
                _uiState.value = PokemonUiState.Loading
            }

            try {
                val fastList = repository.fetchPokemonListFast(_selectedGen.value, _searchQuery.value)
                _uiState.value = PokemonUiState.Success(
                    pokemonList = fastList,
                    activeGen = _selectedGen.value,
                    query = _searchQuery.value
                )

                repository.enrichPokemonDetailsInBackground(fastList) { updatedList ->
                    val current = _uiState.value
                    if (current is PokemonUiState.Success && current.activeGen == _selectedGen.value && current.query == _searchQuery.value) {
                        _uiState.value = current.copy(pokemonList = updatedList)
                    }
                }
            } catch (e: Exception) {
                val current = _uiState.value
                if (current !is PokemonUiState.Success) {
                    _uiState.value = PokemonUiState.Error(e.localizedMessage ?: "Erro ao carregar Pokémon")
                }
            }
        }
    }

    fun loadPokemonDetail(id: Int) {
        loadPokemonDetailByNameOrId(id.toString())
    }

    fun loadPokemonDetailByNameOrId(idOrName: String) {
        _isShiny.value = false
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            val cachedDetail = repository.getCachedDetailLocally(idOrName)
            if (cachedDetail != null) {
                val allMoves = cachedDetail.moves
                val half = (allMoves.size + 1) / 2
                val initialData = PokemonDetailData(
                    detail = cachedDetail,
                    mainAbilities = cachedDetail.abilities.filter { !it.isHidden }.map { it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() } }.ifEmpty { listOf("Nenhuma") },
                    hiddenAbilities = cachedDetail.abilities.filter { it.isHidden }.map { it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() } },
                    damageMoves = allMoves.take(half),
                    supportMoves = allMoves.drop(half),
                    evolutionSteps = emptyList(),
                    pokedexDescriptions = listOf("Carregando descrição da Pokédex..."),
                    hasEvolution = true
                )
                _detailUiState.value = PokemonDetailUiState.Success(data = initialData)
            } else {
                _detailUiState.value = PokemonDetailUiState.Loading
            }

            try {
                coroutineScope {
                    val detailDeferred = async { repository.fetchPokemonDetailByNameOrId(idOrName) }
                    val detail = detailDeferred.await()

                    val chainDeferred = async { repository.fetchEvolutionChain(detail.id) }
                    val descDeferred = async { repository.fetchPokedexDescriptions(detail.id) }

                    val chain = chainDeferred.await()
                    val pokedexDescriptions = descDeferred.await()

                    val mainAbilities = detail.abilities
                        .filter { !it.isHidden }
                        .map { it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() } }

                    val hiddenAbilities = detail.abilities
                        .filter { it.isHidden }
                        .map { it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() } }

                    val evolutionSteps = if (chain != null) extractEvolutionSteps(chain) else emptyList()
                    val hasEvolution = evolutionSteps.isNotEmpty()

                    val allMoves = detail.moves
                    val half = (allMoves.size + 1) / 2
                    val damageMoves = allMoves.take(half)
                    val supportMoves = allMoves.drop(half)

                    val fullData = PokemonDetailData(
                        detail = detail,
                        mainAbilities = mainAbilities.ifEmpty { listOf("Nenhuma") },
                        hiddenAbilities = hiddenAbilities,
                        damageMoves = damageMoves,
                        supportMoves = supportMoves,
                        evolutionSteps = evolutionSteps,
                        pokedexDescriptions = pokedexDescriptions,
                        hasEvolution = hasEvolution
                    )

                    _detailUiState.value = PokemonDetailUiState.Success(data = fullData)
                }
            } catch (e: Exception) {
                if (_detailUiState.value !is PokemonDetailUiState.Success) {
                    _detailUiState.value = PokemonDetailUiState.Error(e.localizedMessage ?: "Erro ao carregar detalhes")
                }
            }
        }
    }

    fun selectTab(tab: DetailTab) {
        val current = _detailUiState.value
        if (current is PokemonDetailUiState.Success) {
            if (tab == DetailTab.EVOL && !current.data.hasEvolution) {
                return
            }
            _detailUiState.value = current.copy(selectedTab = tab)
        }
    }

    fun loadMoveDetail(moveNameOrId: String) {
        viewModelScope.launch {
            _moveDetailUiState.value = MoveDetailUiState.Loading
            try {
                val moveDetail = repository.fetchMoveDetail(moveNameOrId)
                _moveDetailUiState.value = MoveDetailUiState.Success(moveDetail)
            } catch (e: Exception) {
                _moveDetailUiState.value = MoveDetailUiState.Error(e.localizedMessage ?: "Erro ao carregar detalhes do movimento")
            }
        }
    }

    fun clearMoveDetail() {
        _moveDetailUiState.value = MoveDetailUiState.Idle
    }

    fun clearPokemonDetail() {
        _detailUiState.value = PokemonDetailUiState.Idle
    }
}
