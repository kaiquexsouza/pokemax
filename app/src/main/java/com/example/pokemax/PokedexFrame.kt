package com.example.pokemax

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PokedexFrameContainer(
    modifier: Modifier = Modifier,
    theme: PokedexThemeEnum = PokedexThemeEnum.RED_CLASSIC,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LedPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val frameBrush = Brush.linearGradient(
        colors = listOf(
            theme.frameColor,
            theme.frameDarkColor,
            theme.frameColor
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(frameBrush)
            .padding(4.dp)
            .border(1.5.dp, theme.trimColor.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            // Futuristic Top Optical Sensor & Indicator Lights
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // High-Tech Optical Sensor Lens with Glow
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White,
                                    theme.ledColor.copy(alpha = pulseAlpha),
                                    theme.frameDarkColor
                                )
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.TopStart)
                            .offset(x = 4.dp, y = 4.dp)
                    )
                }

                // Mini Indicator Lights
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEAB308))
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Futuristic Inner Glass Screen Frame
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.screenBgColor)
                    .border(1.dp, theme.cyanAccent.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cyanColor = theme.cyanAccent.copy(alpha = 0.25f)
                    val arcStroke = 8f

                    drawArc(
                        color = cyanColor,
                        startAngle = 90f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.40f, -h * 0.05f),
                        size = Size(w * 0.70f, w * 0.70f),
                        style = Stroke(width = arcStroke)
                    )

                    drawArc(
                        color = cyanColor,
                        startAngle = 270f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(-w * 0.40f, h * 0.72f),
                        size = Size(w * 0.70f, w * 0.70f),
                        style = Stroke(width = arcStroke)
                    )
                }

                content()
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.trimColor)
            )
        }
    }
}
