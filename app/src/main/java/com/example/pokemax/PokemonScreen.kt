package com.example.pokemax

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokemax.ui.theme.*

@Composable
fun PokemonScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = viewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn
    val uiState by viewModel.uiState
    val detailUiState by viewModel.detailUiState
    val moveDetailUiState by viewModel.moveDetailUiState

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSubmitted = { usernameOrEmail, password ->
                viewModel.performLogin(usernameOrEmail, password)
            },
            onGuestLogin = {
                viewModel.loginAsGuest()
            },
            modifier = modifier
        )
    } else {
        PokedexFrameContainer(modifier = modifier) {
            if (detailUiState !is PokemonDetailUiState.Idle) {
                PokemonDetailContent(
                    detailUiState = detailUiState,
                    isShiny = viewModel.isShiny.value,
                    onToggleShiny = { viewModel.toggleShiny() },
                    onBackClicked = { viewModel.clearPokemonDetail() },
                    onTabSelected = { viewModel.selectTab(it) },
                    onMoveClicked = { moveName -> viewModel.loadMoveDetail(moveName) },
                    onPokemonClick = { idOrName -> viewModel.loadPokemonDetailByNameOrId(idOrName) },
                    onRetryClicked = { viewModel.clearPokemonDetail() }
                )
            } else {
                PokemonListContent(
                    uiState = uiState,
                    selectedGen = viewModel.selectedGen.value,
                    searchQuery = viewModel.searchQuery.value,
                    onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    onGenSelected = { viewModel.onGenerationSelected(it) },
                    onPokemonClicked = { pokemonId -> viewModel.loadPokemonDetail(pokemonId) },
                    onRetryClicked = { viewModel.loadPokemonList() }
                )
            }

            if (moveDetailUiState !is MoveDetailUiState.Idle) {
                MoveDetailDialog(
                    moveDetailUiState = moveDetailUiState,
                    onDismiss = { viewModel.clearMoveDetail() }
                )
            }
        }
    }
}

@Composable
fun PokemonListContent(
    uiState: PokemonUiState,
    selectedGen: GenerationInfo?,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onGenSelected: (GenerationInfo) -> Unit,
    onPokemonClicked: (Int) -> Unit,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Pokémax",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search Pokémon by name or number", color = Color.White.copy(alpha = 0.7f)) },
            leadingIcon = { Text("🔍") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Text("✕", color = Color.White)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PokedexBlueDark.copy(alpha = 0.85f),
                unfocusedContainerColor = PokedexBlueDark.copy(alpha = 0.75f),
                focusedBorderColor = PokedexCyanAccent,
                unfocusedBorderColor = PokedexCyanAccent.copy(alpha = 0.6f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            items(PokemonGenerations.ALL) { gen ->
                val isSelected = (gen.id == selectedGen?.id)
                FilterChip(
                    selected = isSelected,
                    onClick = { onGenSelected(gen) },
                    label = { Text(text = "${gen.name} (${gen.regionName})", color = Color.White) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PokedexCyanAccent,
                        selectedLabelColor = Color.White,
                        containerColor = PokedexBlueDark.copy(alpha = 0.75f),
                        labelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = PokedexCyanAccent.copy(alpha = 0.8f),
                        selectedBorderColor = PokedexCyanAccent
                    )
                )
            }
        }

        when (uiState) {
            is PokemonUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is PokemonUiState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedGen != null) "Geração ${selectedGen.name}" else "Todas as Gerações",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${uiState.pokemonList.size} exibidos",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                if (uiState.pokemonList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum Pokémon encontrado.",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.pokemonList, key = { it.id }) { pokemon ->
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = { onPokemonClicked(pokemon.id) }
                            )
                        }
                    }
                }
            }
            is PokemonUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Erro: ${uiState.message}",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetryClicked) {
                        Text("Tentar novamente")
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonDetailContent(
    detailUiState: PokemonDetailUiState,
    isShiny: Boolean,
    onToggleShiny: () -> Unit,
    onBackClicked: () -> Unit,
    onTabSelected: (DetailTab) -> Unit,
    onMoveClicked: (String) -> Unit,
    onPokemonClick: (String) -> Unit,
    onRetryClicked: () -> Unit
) {
    when (detailUiState) {
        is PokemonDetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        is PokemonDetailUiState.Success -> {
            val data = detailUiState.data
            val pokemon = data.detail

            Column(modifier = Modifier.fillMaxSize()) {
                PokemonDetailHeader(
                    pokemon = pokemon,
                    isShiny = isShiny,
                    onToggleShiny = onToggleShiny,
                    onBackClicked = onBackClicked
                )

                PokemonDetailTabs(
                    selectedTab = detailUiState.selectedTab,
                    hasEvolution = data.hasEvolution,
                    onTabSelected = onTabSelected,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (detailUiState.selectedTab) {
                        DetailTab.STATS -> {
                            item {
                                PokedexDescriptionCard(
                                    descriptions = data.pokedexDescriptions,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Text(
                                    text = "STATS DE BASE",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                pokemon.stats.forEach { statSlot ->
                                    StatBar(
                                        statName = statSlot.stat.name,
                                        value = statSlot.baseStat
                                    )
                                }
                            }
                        }
                        DetailTab.MOVES -> {
                            item {
                                Text(
                                    text = "Habilidade(s) principal(is)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    data.mainAbilities.forEach { ability ->
                                        AssistChip(
                                            onClick = {},
                                            label = {
                                                Text(
                                                    text = ability,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = Color.White.copy(alpha = 0.90f),
                                                labelColor = Color.Black
                                            ),
                                            border = AssistChipDefaults.assistChipBorder(
                                                enabled = true,
                                                borderColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }

                            if (data.hiddenAbilities.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Habilidade(s) oculta(s)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        data.hiddenAbilities.forEach { ability ->
                                            AssistChip(
                                                onClick = {},
                                                label = {
                                                    Text(
                                                        text = ability,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Black
                                                    )
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = AssistChipDefaults.assistChipColors(
                                                    containerColor = Color.White.copy(alpha = 0.90f),
                                                    labelColor = Color.Black
                                                ),
                                                border = AssistChipDefaults.assistChipBorder(
                                                    enabled = true,
                                                    borderColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            if (data.damageMoves.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Ataque(s) de Dano",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                    )
                                }

                                items(data.damageMoves) { moveSlot ->
                                    val moveName = translateMoveName(moveSlot.move.name)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onMoveClicked(moveSlot.move.name) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.90f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = moveName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Ver detalhes →",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            if (data.supportMoves.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Ataque(s) de Suporte",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                    )
                                }

                                items(data.supportMoves) { moveSlot ->
                                    val moveName = translateMoveName(moveSlot.move.name)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onMoveClicked(moveSlot.move.name) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.90f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = moveName,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Ver detalhes →",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        DetailTab.EVOL -> {
                            item {
                                Text(
                                    text = "CADEIA EVOLUTIVA",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                EvolutionChainView(
                                    evolutionSteps = data.evolutionSteps,
                                    onPokemonClick = onPokemonClick
                                )
                            }
                        }
                    }
                }
            }
        }
        is PokemonDetailUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Erro: ${detailUiState.message}",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetryClicked) {
                    Text("Voltar")
                }
            }
        }
        else -> {}
    }
}
