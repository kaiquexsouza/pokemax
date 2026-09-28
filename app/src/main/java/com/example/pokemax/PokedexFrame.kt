package com.example.pokemax

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.pokemax.ui.theme.*

@Composable
fun PokedexFrameContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PokedexRedFrame)
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val strokeW = 10f

            drawLine(
                color = PokedexRedDark,
                start = Offset(4f, height * 0.15f),
                end = Offset(4f, height * 0.35f),
                strokeWidth = strokeW
            )
            drawLine(
                color = PokedexRedDark,
                start = Offset(4f, height * 0.60f),
                end = Offset(4f, height * 0.80f),
                strokeWidth = strokeW
            )

            drawLine(
                color = PokedexRedDark,
                start = Offset(width - 4f, height * 0.20f),
                end = Offset(width - 4f, height * 0.40f),
                strokeWidth = strokeW
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(PokedexBlueScreen)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val cyanColor = PokedexCyanAccent.copy(alpha = 0.45f)
                    val arcStroke = 12f

                    drawArc(
                        color = cyanColor,
                        startAngle = 90f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(w * 0.35f, -h * 0.05f),
                        size = Size(w * 0.75f, w * 0.75f),
                        style = Stroke(width = arcStroke)
                    )

                    drawArc(
                        color = cyanColor,
                        startAngle = 270f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(-w * 0.35f, h * 0.70f),
                        size = Size(w * 0.75f, w * 0.75f),
                        style = Stroke(width = arcStroke)
                    )

                    val pbRadius = w * 0.14f
                    val pbCenter = Offset(w * 0.80f, h * 0.85f)

                    drawCircle(
                        color = cyanColor,
                        radius = pbRadius,
                        center = pbCenter,
                        style = Stroke(width = arcStroke)
                    )
                    drawLine(
                        color = cyanColor,
                        start = Offset(pbCenter.x - pbRadius, pbCenter.y),
                        end = Offset(pbCenter.x + pbRadius, pbCenter.y),
                        strokeWidth = arcStroke
                    )
                    drawCircle(
                        color = cyanColor,
                        radius = pbRadius * 0.35f,
                        center = pbCenter,
                        style = Stroke(width = arcStroke)
                    )
                }

                content()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp, topStart = 4.dp, topEnd = 4.dp))
                    .background(PokedexSilverTrim)
            )
        }
    }
}
