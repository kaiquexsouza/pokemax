package com.example.pokemax

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

@Composable
fun PokemonTypeBadge(
    typeName: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 11
) {
    val bgColor = getPokemonTypeColor(typeName)
    val ptName = translatePokemonType(typeName)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ptName,
            color = Color.White,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryType = pokemon.details?.primaryType
    val typeColor = getPokemonTypeColor(primaryType)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${pokemon.id}",
                    color = Color.DarkGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val types = pokemon.details?.types
                    if (!types.isNullOrEmpty()) {
                        types.forEach { slot ->
                            PokemonTypeBadge(
                                typeName = slot.type.name,
                                fontSize = 9
                            )
                        }
                    } else {
                        PokemonTypeBadge(typeName = "normal", fontSize = 9)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.pixelArtUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier.size(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(typeColor)
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun PokemonDetailHeader(
    pokemon: PokemonDetail,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = getPokemonTypeColor(pokemon.primaryType)
    val context = LocalContext.current

    val gifRequest = remember(pokemon.id) {
        ImageRequest.Builder(context)
            .data(pokemon.animatedGifUrl)
            .decoderFactory(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoderDecoder.Factory()
                } else {
                    GifDecoder.Factory()
                }
            )
            .crossfade(true)
            .build()
    }

    var currentImageModel by remember(pokemon.id) { mutableStateOf<Any>(gifRequest) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(typeColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "←",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = pokemon.name.uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = pokemon.formattedId,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AsyncImage(
            model = currentImageModel,
            contentDescription = pokemon.name,
            onError = {
                if (currentImageModel != pokemon.fallbackImageUrl) {
                    currentImageModel = pokemon.fallbackImageUrl
                }
            },
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = pokemon.formattedWeight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Peso",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pokemon.types.forEach { slot ->
                        PokemonTypeBadge(typeName = slot.type.name, fontSize = 11)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = pokemon.formattedHeight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Altura",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun PokemonDetailTabs(
    selectedTab: DetailTab,
    hasEvolution: Boolean,
    onTabSelected: (DetailTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableTabs = if (hasEvolution) {
        listOf(DetailTab.STATS, DetailTab.MOVES, DetailTab.EVOL)
    } else {
        listOf(DetailTab.STATS, DetailTab.MOVES)
    }

    val selectedIndex = availableTabs.indexOf(selectedTab).coerceAtLeast(0)

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        contentColor = Color.White
    ) {
        availableTabs.forEach { tab ->
            Tab(
                selected = (selectedTab == tab),
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.name,
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == tab) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                }
            )
        }
    }
}

@Composable
fun StatBar(
    statName: String,
    value: Int,
    maxVal: Int = 255,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (value.toFloat() / maxVal).coerceIn(0f, 1f),
        label = "StatProgress"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = translateStatName(statName),
            modifier = Modifier.width(70.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.9f)
        )

        Text(
            text = value.toString(),
            modifier = Modifier.width(40.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.width(12.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(CircleShape),
            color = if (value >= 100) Color(0xFF4CAF50) else barColor,
            trackColor = Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun EvolutionChainView(
    evolutionSteps: List<EvolutionStep>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        evolutionSteps.forEach { step ->
            val fromGifRequest = remember(step.fromSpeciesId) {
                ImageRequest.Builder(context)
                    .data("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/${step.fromSpeciesId}.gif")
                    .decoderFactory(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoderDecoder.Factory()
                        } else {
                            GifDecoder.Factory()
                        }
                    )
                    .crossfade(true)
                    .build()
            }
            var fromModel by remember(step.fromSpeciesId) { mutableStateOf<Any>(fromGifRequest) }

            val toGifRequest = remember(step.toSpeciesId) {
                ImageRequest.Builder(context)
                    .data("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/${step.toSpeciesId}.gif")
                    .decoderFactory(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoderDecoder.Factory()
                        } else {
                            GifDecoder.Factory()
                        }
                    )
                    .crossfade(true)
                    .build()
            }
            var toModel by remember(step.toSpeciesId) { mutableStateOf<Any>(toGifRequest) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.90f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Forma Base:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AsyncImage(
                            model = fromModel,
                            contentDescription = step.fromSpeciesName,
                            onError = {
                                fromModel = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${step.fromSpeciesId}.png"
                            },
                            modifier = Modifier.size(70.dp)
                        )
                        Text(
                            text = step.fromSpeciesName.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = step.requirement,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "=",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Resultado:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AsyncImage(
                            model = toModel,
                            contentDescription = step.toSpeciesName,
                            onError = {
                                toModel = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${step.toSpeciesId}.png"
                            },
                            modifier = Modifier.size(70.dp)
                        )
                        Text(
                            text = step.toSpeciesName.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoveDetailDialog(
    moveDetailUiState: MoveDetailUiState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalhes do Ataque",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Text(
                            text = "✕",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (moveDetailUiState) {
                    is MoveDetailUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    }
                    is MoveDetailUiState.Success -> {
                        val move = moveDetailUiState.moveDetail
                        val movePtName = translateMoveName(move.name)

                        Text(
                            text = movePtName.uppercase(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PokemonTypeBadge(typeName = move.type.name)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Gray)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = translateDamageClass(move.damageClass?.name),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Poder", fontSize = 12.sp, color = Color.Gray)
                                Text(text = move.power?.toString() ?: "--", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Precisão", fontSize = 12.sp, color = Color.Gray)
                                Text(text = if (move.accuracy != null) "${move.accuracy}%" else "--", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "PP", fontSize = 12.sp, color = Color.Gray)
                                Text(text = move.pp?.toString() ?: "--", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "\"${move.description}\"",
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        )

                        if (!move.effectText.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Efeito Secundário: ${move.effectText}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    is MoveDetailUiState.Error -> {
                        Text(
                            text = moveDetailUiState.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
