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

@Composable
fun PokemonScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val detailUiState by viewModel.detailUiState
    val moveDetailUiState by viewModel.moveDetailUiState

    Box(modifier = modifier.fillMaxSize()) {
        if (detailUiState !is PokemonDetailUiState.Idle) {
            PokemonDetailContent(
                detailUiState = detailUiState,
                onBackClicked = { viewModel.clearPokemonDetail() },
                onTabSelected = { viewModel.selectTab(it) },
                onMoveClicked = { moveName -> viewModel.loadMoveDetail(moveName) },
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

@Composable
fun PokemonListContent(
    uiState: PokemonUiState,
    selectedGen: GenerationInfo,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onGenSelected: (GenerationInfo) -> Unit,
    onPokemonClicked: (Int) -> Unit,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Pokémax",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search Pokémon by name or number") },
            leadingIcon = { Text("🔍") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Text("✕")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
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
                val isSelected = gen.id == selectedGen.id
                FilterChip(
                    selected = isSelected,
                    onClick = { onGenSelected(gen) },
                    label = { Text(text = "${gen.name} (${gen.regionName})") },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        when (uiState) {
            is PokemonUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
                        text = "Resultados da Busca",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.pokemonList.size} exibidos",
                        fontSize = 14.sp,
                        color = Color.Gray
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
                            color = Color.Gray
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
                        color = MaterialTheme.colorScheme.error,
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
    onBackClicked: () -> Unit,
    onTabSelected: (DetailTab) -> Unit,
    onMoveClicked: (String) -> Unit,
    onRetryClicked: () -> Unit
) {
    when (detailUiState) {
        is PokemonDetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is PokemonDetailUiState.Success -> {
            val data = detailUiState.data
            val pokemon = data.detail

            Column(modifier = Modifier.fillMaxSize()) {
                PokemonDetailHeader(
                    pokemon = pokemon,
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
                                Text(
                                    text = "STATS DE BASE",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
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
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    data.mainAbilities.forEach { ability ->
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(ability) },
                                            shape = RoundedCornerShape(12.dp)
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
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        data.hiddenAbilities.forEach { ability ->
                                            AssistChip(
                                                onClick = {},
                                                label = { Text(ability) },
                                                shape = RoundedCornerShape(12.dp)
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
                                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                    )
                                }

                                items(data.damageMoves) { moveSlot ->
                                    val moveName = moveSlot.move.name.replace('-', ' ').replaceFirstChar { it.uppercase() }
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onMoveClicked(moveSlot.move.name) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
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
                                                fontWeight = FontWeight.SemiBold
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
                                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                    )
                                }

                                items(data.supportMoves) { moveSlot ->
                                    val moveName = moveSlot.move.name.replace('-', ' ').replaceFirstChar { it.uppercase() }
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onMoveClicked(moveSlot.move.name) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
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
                                                fontWeight = FontWeight.SemiBold
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
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                EvolutionChainView(
                                    evolutionSteps = data.evolutionSteps
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
                    color = MaterialTheme.colorScheme.error,
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
