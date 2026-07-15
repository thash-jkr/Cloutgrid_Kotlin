package com.cloutgrid.androidapp.ui.screens.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box

@Composable
private fun ShimmerBox(
    modifier: Modifier = Modifier,
    animationOffset: Float
) {
    var widthPx by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Gray.copy(alpha = 0.3f))
            .onGloballyPositioned { widthPx = it.size.width.toFloat() }
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.7f),
                        Color.Transparent
                    ),
                    start = Offset(animationOffset * widthPx, 0f),
                    end = Offset(animationOffset * widthPx + widthPx, 0f)
                )
            )
    )
}

@Composable
private fun LoadingBox(animationOffset: Float) {
    Column {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            animationOffset = animationOffset
        )

        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(300.dp),
            animationOffset = animationOffset
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .height(50.dp),
                animationOffset = animationOffset
            )

            ShimmerBox(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .height(50.dp),
                animationOffset = animationOffset
            )
        }
    }
}

@Composable
fun FeedLoading() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val animationOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Column {
        LoadingBox(animationOffset = animationOffset)
        LoadingBox(animationOffset = animationOffset)
        LoadingBox(animationOffset = animationOffset)
    }
}