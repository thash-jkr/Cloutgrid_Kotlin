package com.cloutgrid.androidapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cloutgrid.androidapp.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
            .background(Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Empty State Graphic",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(150.dp)
                .padding(10.dp)
        )

        if (isLoading) {
            LoadingIndicator()
        } else {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}