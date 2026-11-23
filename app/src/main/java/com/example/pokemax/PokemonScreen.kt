package com.example.pokemax

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokemax.ui.theme.*

@Composable
fun PokemonScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonViewModel = viewModel()
) {
    val context = LocalContext.current
    viewModel.initThemeManager(context)

    val isLoggedIn by viewModel.isLoggedIn
    val uiState by viewModel.uiState
    val detailUiState by viewModel.detailUiState
    val moveDetailUiState by viewModel.moveDetailUiState
    val isQuizOpen by viewModel.isQuizOpen
    val selectedTheme by viewModel.selectedTheme
    val isThemeSelectorOpen by viewModel.isThemeSelectorOpen

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSubmitted = { usernameOrEmail, password ->
                viewModel.performLogin(usernameOrEmail, password)
            },
            modifier = modifier,
            theme = selectedTheme
        )
    } else {
        PokedexFrameContainer(modifier = modifier, theme = selectedTheme) {
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
                    onOpenQuiz = { viewModel.openQuiz() },
                    onOpenThemeSelector = { viewModel.openThemeSelector() },
                    onLogout = { viewModel.logout() },
                    onRetryClicked = { viewModel.loadPokemonList() }
                )
            }

            if (isQuizOpen) {
                val currentList = (uiState as? PokemonUiState.Success)?.pokemonList ?: emptyList()
                QuizDialog(
                    pokemonList = currentList,
                    onDismiss = { viewModel.closeQuiz() }
                )
            }

            if (isThemeSelectorOpen) {
                ThemeSelectorDialog(
                    selectedTheme = selectedTheme,
                    onThemeSelected = { theme -> viewModel.selectTheme(theme) },
                    onDismiss = { viewModel.closeThemeSelector() }
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
    onOpenQuiz: () -> Unit,
    onOpenThemeSelector: () -> Unit,
    onLogout: () -> Unit,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onLogout,
                shape = RoundedCornerShape(16.dp),
                color = PokedexBlueDark.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, PokedexCyanAccent.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Sair",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Pokémax",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenThemeSelector,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PokedexBlueDark.copy(alpha = 0.85f))
                ) {
                    Text("🎨", fontSize = 18.sp)
                }

                IconButton(
                    onClick = onOpenQuiz,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PokedexBlueDark.copy(alpha = 0.85f))
                ) {
                    Text("🎮", fontSize = 18.sp)
                }
            }
        }

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
                    PokemonLoadingIndicator(size = 120.dp)
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
    val context = LocalContext.current
    val audioPlayerManager = remember(context) { AudioPlayerManager(context) }
    val speechService = remember(context) { PokedexSpeechService(context) }
    var moveSearchQuery by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayerManager.stop()
            speechService.shutdown()
        }
    }

    when (detailUiState) {
        is PokemonDetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                PokemonLoadingIndicator(size = 120.dp)
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
                    onPlayCry = { audioPlayerManager.playPokemonCry(pokemon.id) },
                    onBackClicked = {
                        audioPlayerManager.stop()
                        speechService.stop()
                        onBackClicked()
                    }
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
                                    onSpeakDescription = { text -> speechService.speak(text) },
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

                                Spacer(modifier = Modifier.height(8.dp))

                                TypeEffectivenessCard(
                                    types = pokemon.types,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }
                        }
                        DetailTab.MOVES -> {
                            item {
                                OutlinedTextField(
                                    value = moveSearchQuery,
                                    onValueChange = { moveSearchQuery = it },
                                    placeholder = { Text("Buscar ataque (Português ou Inglês)", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp) },
                                    leadingIcon = { Text("🔍", fontSize = 14.sp) },
                                    trailingIcon = {
                                        if (moveSearchQuery.isNotEmpty()) {
                                            IconButton(onClick = { moveSearchQuery = "" }) {
                                                Text("✕", color = Color.White, fontSize = 14.sp)
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
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
                                        .padding(bottom = 8.dp)
                                )
                            }

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

                            val cleanMoveQuery = moveSearchQuery.trim().lowercase()
                            val filteredDamage = if (cleanMoveQuery.isEmpty()) {
                                data.damageMoves
                            } else {
                                data.damageMoves.filter { moveSlot ->
                                    val enName = moveSlot.move.name.lowercase().replace('-', ' ')
                                    val ptName = translateMoveName(moveSlot.move.name).lowercase()
                                    enName.contains(cleanMoveQuery) || ptName.contains(cleanMoveQuery)
                                }
                            }

                            val filteredSupport = if (cleanMoveQuery.isEmpty()) {
                                data.supportMoves
                            } else {
                                data.supportMoves.filter { moveSlot ->
                                    val enName = moveSlot.move.name.lowercase().replace('-', ' ')
                                    val ptName = translateMoveName(moveSlot.move.name).lowercase()
                                    enName.contains(cleanMoveQuery) || ptName.contains(cleanMoveQuery)
                                }
                            }

                            if (cleanMoveQuery.isNotEmpty() && filteredDamage.isEmpty() && filteredSupport.isEmpty()) {
                                item {
                                    Text(
                                        text = "Nenhum ataque encontrado para \"$moveSearchQuery\".",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                }
                            }

                            if (filteredDamage.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Ataque(s) de Dano (${filteredDamage.size})",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                    )
                                }

                                items(filteredDamage) { moveSlot ->
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

                            if (filteredSupport.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Ataque(s) de Suporte (${filteredSupport.size})",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                                    )
                                }

                                items(filteredSupport) { moveSlot ->
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

@Composable
fun ThemeSelectorDialog(
    selectedTheme: PokedexThemeEnum,
    onThemeSelected: (PokedexThemeEnum) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PokedexBlueDark,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎨 ESCOLHA A POKÉDEX",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PokemonYellow
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .size(32.dp)
                    ) {
                        Text(
                            text = "✕",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PokedexThemeEnum.entries.forEach { theme ->
                        val isSelected = theme == selectedTheme
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable { onThemeSelected(theme) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PokemonYellow else Color.White.copy(alpha = 0.90f)
                            ),
                            border = if (isSelected) BorderStroke(2.dp, PokedexCyanAccent) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(theme.frameColor)
                                    )
                                    Text(
                                        text = theme.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) PokemonBlueDark else Color.Black
                                    )
                                }

                                if (isSelected) {
                                    Text(
                                        text = "✓ Ativo",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PokemonBlueDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
