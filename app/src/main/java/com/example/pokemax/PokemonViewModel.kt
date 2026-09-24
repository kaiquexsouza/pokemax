package com.example.pokemax

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
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

    private val _selectedGen = mutableStateOf<GenerationInfo?>(PokemonGenerations.ALL.first())
    val selectedGen: State<GenerationInfo?> = _selectedGen

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _uiState = mutableStateOf<PokemonUiState>(PokemonUiState.Loading)
    val uiState: State<PokemonUiState> = _uiState

    private val _detailUiState = mutableStateOf<PokemonDetailUiState>(PokemonDetailUiState.Idle)
    val detailUiState: State<PokemonDetailUiState> = _detailUiState

    private val _moveDetailUiState = mutableStateOf<MoveDetailUiState>(MoveDetailUiState.Idle)
    val moveDetailUiState: State<MoveDetailUiState> = _moveDetailUiState

    private var searchJob: Job? = null

    init {
        loadPokemonList()
    }

    fun onGenerationSelected(gen: GenerationInfo) {
        if (_selectedGen.value?.id == gen.id) {
            // Se clicar na mesma geração já selecionada, deseleciona e mostra todas
            _selectedGen.value = null
        } else {
            _selectedGen.value = gen
        }
        loadPokemonList()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce search
            loadPokemonList()
        }
    }

    fun loadPokemonList() {
        viewModelScope.launch {
            _uiState.value = PokemonUiState.Loading
            try {
                val list = repository.fetchPokemonListForGen(_selectedGen.value, _searchQuery.value)
                _uiState.value = PokemonUiState.Success(
                    pokemonList = list,
                    activeGen = _selectedGen.value,
                    query = _searchQuery.value
                )
            } catch (e: Exception) {
                _uiState.value = PokemonUiState.Error(e.localizedMessage ?: "Erro ao carregar Pokémon")
            }
        }
    }

    fun loadPokemonDetail(id: Int) {
        viewModelScope.launch {
            _detailUiState.value = PokemonDetailUiState.Loading
            try {
                val detail = repository.fetchPokemonDetail(id)
                val chain = repository.fetchEvolutionChain(id)

                val mainAbilities = detail.abilities
                    .filter { !it.isHidden }
                    .map { it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() } }

                val hiddenAbilities = detail.abilities
                    .filter { it.isHidden }
                    .map { it.ability.name.replace('-', ' ').replaceFirstChar { c -> c.uppercase() } }

                val evolutionSteps = if (chain != null) extractEvolutionSteps(chain) else emptyList()
                val hasEvolution = evolutionSteps.isNotEmpty()

                // Separate moves into damage and support moves (half and half or by index for preview)
                val allMoves = detail.moves
                val half = (allMoves.size + 1) / 2
                val damageMoves = allMoves.take(half)
                val supportMoves = allMoves.drop(half)

                val data = PokemonDetailData(
                    detail = detail,
                    mainAbilities = mainAbilities.ifEmpty { listOf("Nenhuma") },
                    hiddenAbilities = hiddenAbilities,
                    damageMoves = damageMoves,
                    supportMoves = supportMoves,
                    evolutionSteps = evolutionSteps,
                    hasEvolution = hasEvolution
                )

                _detailUiState.value = PokemonDetailUiState.Success(
                    data = data,
                    selectedTab = DetailTab.STATS
                )
            } catch (e: Exception) {
                _detailUiState.value = PokemonDetailUiState.Error(e.localizedMessage ?: "Erro ao carregar detalhes")
            }
        }
    }

    fun selectTab(tab: DetailTab) {
        val current = _detailUiState.value
        if (current is PokemonDetailUiState.Success) {
            // Se a aba for EVOL mas o pokémon não tiver evolução, mantemos em STATS
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
