package com.cloutgrid.androidapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    // 1. Create an infinite transition state controller
    val infiniteTransition = rememberInfiniteTransition(label = "SettingsSpinnerContext")

    // 2. Animate a rotation degree angle from 0° to 360° continuously
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            // 2000ms (2 seconds) per full turn. Lower this number to make it spin faster!
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SettingsRotation"
    )

    // 3. Render the native Settings Icon wrapped in a rotation graphics layer
    Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "Loading...",
        tint = color,
        modifier = modifier
            .size(size)
            .graphicsLayer {
                // This applies the 0-360 angle to the matrix rendering pipeline
                rotationZ = rotationAngle
            }
    )
}