package com.cloutgrid.androidapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.NotificationModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notifications(
    home: HomeManager = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        home.fetchNotifications()
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Notifications", style = MaterialTheme.typography.titleMedium) }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            if (home.notifications.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp) // Forces rows to lock flush against each other
                ) {
                    itemsIndexed(
                        items = home.notifications,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        val cardShape = when {
                            home.notifications.size == 1 -> RoundedCornerShape(20.dp)
                            index == 0 -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            index == home.notifications.lastIndex -> RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                            else -> RectangleShape
                        }

                        NotificationDismissRow(
                            item = item,
                            shape = cardShape,
                            showDivider = index != home.notifications.lastIndex,
                            onDelete = { home.readNotification(item.id) }
                        )
                    }
                }
            } else {
                EmptyNotificationView(type = "general", message = "No new notifications!")
            }
        }
    }
}

@Composable
fun EmptyNotificationView(type: String, message: String) {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDismissRow(
    item: NotificationModel,
    shape: Shape,
    showDivider: Boolean,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            if (newValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val backgroundColor = when (dismissState.targetValue) {
                SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape) // 🌟 2. Keeps the red swipe background clipped inside the round container corner track
                    .background(backgroundColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape) // 🌟 3. Ensures row content cuts cleanly to fit the container grouping
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = item.message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    // Stripped out leadingContent and supportingContent to isolate text content only
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh // 🌟 Matches the soft light card block color
                    )
                )

                // 🌟 4. Fine hair-line separator between stacked notification items
                if (showDivider) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    )
}