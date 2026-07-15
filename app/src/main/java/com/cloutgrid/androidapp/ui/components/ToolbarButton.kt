package com.cloutgrid.androidapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cloutgrid.androidapp.ui.theme.OffWhite

@Composable
fun ToolbarButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier
            .shadow(
                elevation = 2.dp,
                shape = CircleShape,
                clip = true
            )
            .size(45.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = OffWhite,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = if (contentDescription == "Logo") Modifier.size(45.dp) else Modifier.size(25.dp),
            tint = if (contentDescription == "Logo") {
                Color.Unspecified
            } else {
                LocalContentColor.current
            }
        )
    }
}

@Preview
@Composable
fun ToolbarButtonPreview() {
    ToolbarButton(
        imageVector = Icons.Rounded.Home,
        contentDescription = "Icon",
        onClick = {}
    )
}