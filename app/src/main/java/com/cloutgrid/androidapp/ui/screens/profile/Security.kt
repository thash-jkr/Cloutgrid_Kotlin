package com.cloutgrid.androidapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.ListRow
import com.cloutgrid.androidapp.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Security(
    onNavigateBack: () -> Unit,
    profile: ProfileManager = hiltViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CloutHeader(
                title = "Security",
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    onClick = onNavigateBack
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = "Password",
                        )
                    },
                    onClick = { },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                ) {
                    Text("Change Password")
                }

                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Delete",
                        )
                    },
                    onClick = {
                        showDeleteDialog = true
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                ) {
                    Text("Delete Account")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? " +
                    "This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        profile.handleAccountDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
