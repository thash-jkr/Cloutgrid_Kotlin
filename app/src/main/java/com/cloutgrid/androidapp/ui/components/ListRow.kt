package com.cloutgrid.androidapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ListRow(
    title: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    endIcon: ImageVector? = null,
    danger: Boolean = false
) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "List Item Icon",
                    tint = if (danger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingContent = {
            if (endIcon != null) {
                Icon(
                    imageVector = endIcon,
                    contentDescription = "List end icon",
                    tint = if (danger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = if (danger) MaterialTheme.colorScheme.error else Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}