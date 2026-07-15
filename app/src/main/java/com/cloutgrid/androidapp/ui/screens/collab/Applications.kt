package com.cloutgrid.androidapp.ui.screens.collab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.data.model.JobModel
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.components.CategoryList
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.theme.OffWhite

@Composable
fun Applications(
    job: JobModel,
    collab: CollabManager = hiltViewModel(),
    onNavigateToOtherProfile: (String) -> Unit,
    onNavigateToAnswers: (Int) -> Unit,
    onClose: () -> Unit,
) {
    LaunchedEffect(Unit) {
        collab.fetchApplications(job)
    }

    val applications = collab.applications

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Applications",
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Rounded.Delete,
                        contentDescription = "Go Back",
                        onClick = { showDeleteDialog = true }
                    )
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 15.dp,
                        end = 15.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Column {
                    Text(
                        "Job posted on ${job.date}"
                    )

                    Text(
                        "Target Creator: ${CategoryList.label(job.targetCreator)}"
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    itemsIndexed(applications, key = { _, application -> application.id }) { index, application ->
                        SegmentedListItem(
                            shapes = ListItemDefaults.segmentedShapes(index = index, count = applications.count()),
                            supportingContent = { Text(CategoryList.label(application.creator.area ?: "")) },
                            leadingContent = {
                                AsyncImage(
                                    model = ApiConfig.current.baseURL + application.creator.profile.profilePhoto,
                                    contentDescription = "profile photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = OffWhite),
                            onClick = {
                                if (job.questions.isEmpty()) {
                                    onNavigateToOtherProfile(application.creator.profile.username)
                                } else {
                                    onNavigateToAnswers(
                                        application.id
                                    )
                                }
                            }
                        ) {
                            Text(application.creator.profile.name)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false},
            title = { Text("Delete collab?") },
            text = {
                Text("Are you sure you want to delete this collab? " +
                    "You will lose all data associated with it.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        collab.deleteJob(job.id)
                        showDeleteDialog = false
                        onClose()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }
}