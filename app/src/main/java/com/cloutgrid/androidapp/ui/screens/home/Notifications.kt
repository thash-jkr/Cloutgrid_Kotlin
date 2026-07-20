package com.cloutgrid.androidapp.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.Empty
import com.cloutgrid.androidapp.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notifications(
    home: HomeManager = hiltViewModel()
) {
    val notifications = home.notifications

    LaunchedEffect(notifications) {
        home.fetchNotifications()
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CloutHeader(
                title = "Notifications"
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (notifications.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = innerPadding.calculateTopPadding(),
                        end = 16.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(
                        items = notifications,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            positionalThreshold = { totalDistance -> totalDistance * 0.4f }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            onDismiss = { direction ->
                                if (direction == SwipeToDismissBoxValue.EndToStart) {
                                    home.readNotification(item.id)
                                }
                            },
                            backgroundContent = {
                                val direction = dismissState.dismissDirection
                                val color by animateColorAsState(
                                    targetValue = if (direction == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent,
                                    label = "swipeBackgroundColor"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    if (direction == SwipeToDismissBoxValue.EndToStart) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Dismiss notification",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        ) {
                            SegmentedListItem(
                                shapes = ListItemDefaults.segmentedShapes(index = index, count = notifications.count()),
                            ) {
                                Text(item.message)
                            }
                        }
                    }
                }
            } else {
                Empty(type = "general", message = "No new notifications!")
            }
        }
    }
}