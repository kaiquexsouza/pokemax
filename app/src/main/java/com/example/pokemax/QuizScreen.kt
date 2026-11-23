package com.example.pokemax

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.pokemax.ui.theme.*
import kotlin.random.Random

data class QuizQuestion(
    val targetPokemon: Pokemon,
    val options: List<Pokemon>
)

@Composable
fun QuizDialog(
    pokemonList: List<Pokemon>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (pokemonList.size < 4) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = PokedexBlueDark
            ) {
                Text(
                    text = "Carregue mais Pokémons para iniciar o Quiz!",
                    color = Color.White,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
        return
    }

    val context = LocalContext.current
    val audioPlayerManager = remember(context) { AudioPlayerManager(context) }
    val scoreManager = remember(context) { QuizScoreManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayerManager.stop()
        }
    }

    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var isNewRecord by remember { mutableStateOf(false) }
    var currentQuestion by remember { mutableStateOf(generateQuestion(pokemonList)) }
    var selectedOption by remember { mutableStateOf<Pokemon?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    fun nextQuestion() {
        audioPlayerManager.stop()
        selectedOption = null
        isAnswered = false
        currentQuestion = generateQuestion(pokemonList)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = PokedexBlueDark,
            tonalElevation = 8.dp,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🎮 QUEM É ESSE POKÉMON?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PokemonYellow
                        )
                        Text(
                            text = "Pontos: $score | Combo: 🔥 $streak",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "🏆 Recorde: ${scoreManager.highScore} | 🔥 Máx: ${scoreManager.maxStreak}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PokemonYellow.copy(alpha = 0.9f)
                        )
                    }

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

                if (isNewRecord) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🎉 NOVO RECORDE PESSOAL! 🎉",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PokemonYellow
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card da Silhueta / Revelação
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .border(3.dp, PokedexCyanAccent, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = currentQuestion.targetPokemon.pixelArtUrl,
                        contentDescription = "Quiz Pokemon",
                        colorFilter = if (isAnswered) null else ColorFilter.tint(Color.Black),
                        modifier = Modifier.size(130.dp)
                    )

                    if (isAnswered) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(if (selectedOption?.id == currentQuestion.targetPokemon.id) Color(0xFF4CAF50) else Color(0xFFE53935))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (selectedOption?.id == currentQuestion.targetPokemon.id) "✓ Correto!" else "✕ Errou!",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isAnswered) {
                        "É o ${currentQuestion.targetPokemon.name.replaceFirstChar { it.uppercase() }}!"
                    } else {
                        "Adivinhe o Pokémon acima:"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4 Opções de Resposta
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currentQuestion.options.forEach { option ->
                        val isCorrect = option.id == currentQuestion.targetPokemon.id
                        val isSelected = option.id == selectedOption?.id

                        val containerColor = when {
                            !isAnswered -> Color.White.copy(alpha = 0.90f)
                            isCorrect -> Color(0xFF4CAF50)
                            isSelected && !isCorrect -> Color(0xFFE53935)
                            else -> Color.White.copy(alpha = 0.40f)
                        }

                        val textColor = when {
                            !isAnswered -> Color.Black
                            isCorrect || (isSelected && !isCorrect) -> Color.White
                            else -> Color.DarkGray
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clickable(enabled = !isAnswered) {
                                    selectedOption = option
                                    isAnswered = true
                                    if (isCorrect) {
                                        score += 10 + (streak * 2)
                                        streak++
                                        val newRec = scoreManager.updateScore(score, streak)
                                        if (newRec) {
                                            isNewRecord = true
                                        }
                                        audioPlayerManager.playPokemonCry(option.id)
                                    } else {
                                        streak = 0
                                        scoreManager.updateScore(score, streak)
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.name.replaceFirstChar { it.uppercase() },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(visible = isAnswered) {
                    Button(
                        onClick = { nextQuestion() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PokemonYellow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "PRÓXIMO POKÉMON →",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PokemonBlueDark
                        )
                    }
                }
            }
        }
    }
}

private fun generateQuestion(pokemonList: List<Pokemon>): QuizQuestion {
    val target = pokemonList.random()
    val wrongOptions = pokemonList.filter { it.id != target.id }.shuffled().take(3)
    val allOptions = (wrongOptions + target).shuffled()
    return QuizQuestion(targetPokemon = target, options = allOptions)
}
