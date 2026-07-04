package com.cloutgrid.androidapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cloutgrid.androidapp.data.model.HeaderAction
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloutHeader(
    title: String? = null,
    icon: HeaderAction? = null,
    actions: List<HeaderAction> = emptyList()
) {
    CenterAlignedTopAppBar(
        title = {
            if (title != null) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        navigationIcon = {
            if (icon != null) {
                Row {
                    Spacer(modifier = Modifier.width(15.dp))

                    ToolbarButton(
                        imageVector = icon.icon,
                        contentDescription = icon.contentDescription,
                        onClick = icon.onClick
                    )
                }
            }
        },
        actions = {
            actions.forEach { action ->
                var menuExpanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    ToolbarButton(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription,
                        onClick = {
                            if (action.menuItems != null) {
                                menuExpanded = true
                            } else {
                                action.onClick()
                            }
                        }
                    )

                    if (action.menuItems != null) {
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .background(Color.White)
                        ) {
                            action.menuItems.forEachIndexed { idx, menuItem ->
                                DropdownMenuItem(
                                    text = { Text(menuItem.title) },
                                    leadingIcon = {
                                        Icon(
                                            menuItem.icon,
                                            contentDescription = "Dropdown Icon"
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        menuItem.onClick()
                                    }
                                )

                                if (idx != action.menuItems.lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(15.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = if (actions.isEmpty() && icon == null) {
                    listOf(
                        Color.White.copy(alpha = 1f),
                        Color.White.copy(alpha = 0.8f),
                        Color.White.copy(alpha = 0.0f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.0f)
                    )
                }
            )
        )
    )
}
