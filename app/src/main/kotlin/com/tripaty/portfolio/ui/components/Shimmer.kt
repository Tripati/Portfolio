package com.tripaty.portfolio.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerTranslate",
    )
    val base = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val highlight = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(translate.value - 200f, 0f),
        end = Offset(translate.value, 0f),
    )
}

@Composable
fun ShimmerBlock(modifier: Modifier = Modifier, height: Int = 16) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(shimmerBrush()),
    )
}

@Composable
fun PortfolioLoadingShimmer() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ShimmerBlock(height = 28)
        ShimmerBlock(height = 14)
        ShimmerBlock(height = 14)
        Spacer(Modifier.height(8.dp))
        repeat(4) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(shimmerBrush()))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShimmerBlock(height = 14)
                    ShimmerBlock(height = 12)
                }
            }
        }
    }
}
