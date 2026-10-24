package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.CurrencyType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-definition country flag badges rendered natively with Jetpack Compose Canvas.
 */
@Composable
fun CountryFlagBadge(
    currency: CurrencyType,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(width = size * 1.35f, height = size)
            .shadow(2.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        when (currency) {
            CurrencyType.BRL -> BrazilFlag(Modifier.fillMaxSize())
            CurrencyType.UYU -> UruguayFlag(Modifier.fillMaxSize())
            CurrencyType.USD -> UsaFlag(Modifier.fillMaxSize())
        }
    }
}

/**
 * Authentic Flag of Brazil:
 * - Emerald green field
 * - Yellow centered rhombus (diamond)
 * - Deep navy blue celestial sphere
 * - White celestial equator band
 */
@Composable
fun BrazilFlag(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Green background
        drawRect(color = Color(0xFF009B3A), size = size)

        // 2. Yellow rhombus
        val rhombusMarginX = w * 0.09f
        val rhombusMarginY = h * 0.12f

        val rhombusPath = Path().apply {
            moveTo(w / 2f, rhombusMarginY)
            lineTo(w - rhombusMarginX, h / 2f)
            lineTo(w / 2f, h - rhombusMarginY)
            lineTo(rhombusMarginX, h / 2f)
            close()
        }
        drawPath(path = rhombusPath, color = Color(0xFFFFDF00))

        // 3. Dark Blue Circle
        val circleRadius = h * 0.26f
        val centerPoint = Offset(w / 2f, h / 2f)
        drawCircle(
            color = Color(0xFF002776),
            radius = circleRadius,
            center = centerPoint
        )

        // 4. White curved band across the circle
        val arcCenter = Offset(w / 2f, h * 0.72f)
        val arcRadius = circleRadius * 1.25f
        val bandPath = Path().apply {
            arcTo(
                rect = Rect(
                    left = arcCenter.x - arcRadius,
                    top = arcCenter.y - arcRadius,
                    right = arcCenter.x + arcRadius,
                    bottom = arcCenter.y + arcRadius
                ),
                startAngleDegrees = 205f,
                sweepAngleDegrees = 52f,
                forceMoveTo = false
            )
        }
        drawPath(
            path = bandPath,
            color = Color.White,
            style = Stroke(width = h * 0.045f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Authentic Flag of Uruguay:
 * - 9 alternating horizontal stripes (5 white, 4 azure blue)
 * - White square canton at top-left (height of 5 stripes)
 * - Golden Sun of May (Sol de Mayo) with radiating rays
 */
@Composable
fun UruguayFlag(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stripeHeight = h / 9f

        // Draw 9 stripes
        for (i in 0 until 9) {
            val color = if (i % 2 == 0) Color.White else Color(0xFF0081C8)
            drawRect(
                color = color,
                topLeft = Offset(0f, i * stripeHeight),
                size = Size(w, stripeHeight)
            )
        }

        // Canton in top-left (spans first 5 stripes, square)
        val cantonHeight = stripeHeight * 5f
        val cantonWidth = cantonHeight
        drawRect(
            color = Color.White,
            topLeft = Offset(0f, 0f),
            size = Size(cantonWidth, cantonHeight)
        )
        // Subtle canton right border to separate cleanly
        drawLine(
            color = Color(0x330081C8),
            start = Offset(cantonWidth, 0f),
            end = Offset(cantonWidth, cantonHeight),
            strokeWidth = 1f
        )

        // Sol de Mayo in canton center
        val sunCenter = Offset(cantonWidth / 2f, cantonHeight / 2f)
        val sunRadius = cantonHeight * 0.22f

        // Sun rays (16 rays alternating)
        val rayInner = sunRadius * 1.05f
        val rayOuter = sunRadius * 1.85f
        for (i in 0 until 16) {
            val angle = (i * 22.5 * PI / 180.0).toFloat()
            val startX = sunCenter.x + rayInner * cos(angle)
            val startY = sunCenter.y + rayInner * sin(angle)
            val endX = sunCenter.x + rayOuter * cos(angle)
            val endY = sunCenter.y + rayOuter * sin(angle)
            drawLine(
                color = Color(0xFFEAB308),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (i % 2 == 0) 2.2f else 1.4f,
                cap = StrokeCap.Round
            )
        }

        // Sun face disc
        drawCircle(
            color = Color(0xFFFACC15),
            radius = sunRadius,
            center = sunCenter
        )
        drawCircle(
            color = Color(0xFFCA8A04),
            radius = sunRadius,
            center = sunCenter,
            style = Stroke(width = 1.2f)
        )

        // Friendly facial detail dots for the Sun of May
        val eyeOffsetY = sunRadius * 0.2f
        val eyeOffsetX = sunRadius * 0.35f
        drawCircle(
            color = Color(0xFF78350F),
            radius = sunRadius * 0.12f,
            center = Offset(sunCenter.x - eyeOffsetX, sunCenter.y - eyeOffsetY)
        )
        drawCircle(
            color = Color(0xFF78350F),
            radius = sunRadius * 0.12f,
            center = Offset(sunCenter.x + eyeOffsetX, sunCenter.y - eyeOffsetY)
        )
        // Smile
        val mouthPath = Path().apply {
            moveTo(sunCenter.x - sunRadius * 0.3f, sunCenter.y + sunRadius * 0.3f)
            quadraticTo(
                sunCenter.x, sunCenter.y + sunRadius * 0.55f,
                sunCenter.x + sunRadius * 0.3f, sunCenter.y + sunRadius * 0.3f
            )
        }
        drawPath(
            path = mouthPath,
            color = Color(0xFF78350F),
            style = Stroke(width = 1.5f, cap = StrokeCap.Round)
        )
    }
}

/**
 * Authentic Flag of the United States:
 * - 13 alternating horizontal stripes (7 red, 6 white)
 * - Dark blue canton at top-left
 * - White stars grid representation
 */
@Composable
fun UsaFlag(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stripeHeight = h / 13f

        // 13 stripes
        for (i in 0 until 13) {
            val color = if (i % 2 == 0) Color(0xFFB22234) else Color.White
            drawRect(
                color = color,
                topLeft = Offset(0f, i * stripeHeight),
                size = Size(w, stripeHeight)
            )
        }

        // Blue Canton (spans 7 stripes high, ~40% width)
        val cantonHeight = stripeHeight * 7f
        val cantonWidth = w * 0.42f
        drawRect(
            color = Color(0xFF3C3B6E),
            topLeft = Offset(0f, 0f),
            size = Size(cantonWidth, cantonHeight)
        )

        // Grid of white stars/dots
        val rows = 5
        val cols = 6
        val stepX = cantonWidth / (cols + 1)
        val stepY = cantonHeight / (rows + 1)

        for (r in 1..rows) {
            val isOffset = (r % 2 == 0)
            val currentCols = if (isOffset) 5 else 6
            val startX = if (isOffset) stepX * 1.5f else stepX
            for (c in 0 until currentCols) {
                drawCircle(
                    color = Color.White,
                    radius = stripeHeight * 0.22f,
                    center = Offset(startX + c * stepX, r * stepY)
                )
            }
        }
    }
}
