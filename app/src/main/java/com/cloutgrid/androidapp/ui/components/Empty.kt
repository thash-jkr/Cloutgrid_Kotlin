package com.cloutgrid.androidapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cloutgrid.androidapp.R

@Composable
fun Empty(
    type: String,
    message: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val imageRes = when (type) {
        "post" -> R.drawable.post
        "collab" -> R.drawable.collab
        "comment" -> R.drawable.comments
        else -> R.drawable.general
    }

    Column(
        modifier = modifier
            .fillMaxSize(), // Maps to .frame(maxWidth: .infinity, maxHeight: .infinity)
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp) // Maps to VStack(spacing: 10)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Empty State Graphic",
            contentScale = ContentScale.Fit, // Maps to .aspectRatio(contentMode: .fit)
            modifier = Modifier
                .width(150.dp)
                .padding(10.dp)
        )

        if (isLoading) {
            LoadingSpinner()
        } else {
            Text(
                text = message,
                // Maps closely to SwiftUI's .callout typography
                style = MaterialTheme.typography.bodyMedium,
                // Maps to SwiftUI's .secondary color palette
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 🌟 Maps to Spacer() inside a VStack to push elements towards the top bounds
        Spacer(modifier = Modifier.weight(1f))
    }
}