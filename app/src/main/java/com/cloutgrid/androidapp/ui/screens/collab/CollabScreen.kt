package com.cloutgrid.androidapp.ui.screens.collab

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.JobModel
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.components.CategoryList
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import com.cloutgrid.androidapp.ui.components.Empty

@Composable
fun CollabScreen(
    collab: CollabManager = hiltViewModel(),
    scaffoldPadding: PaddingValues,
    onNavigateToQuestions: (Int) -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
    onNavigateToAnswers: (Int) -> Unit,
) {
    var selectedJob by remember { mutableStateOf<JobModel?>(null) }
    val context = LocalContext.current
    var isRefreshing by remember { mutableStateOf(false) }

    val type by collab.type.collectAsState()

    LaunchedEffect(Unit) {
        collab.events.collect { success ->
            if (success) {
                Toast.makeText(context, collab.successMessage, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, collab.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(type) {
        if (collab.jobs.isEmpty()) {
            if (type == "creator") {
                collab.fetchJobs()
            } else {
                collab.fetchBusinessJobs()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Collaborate",
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true

                if (type == "creator") {
                    collab.fetchJobs()
                } else {
                    collab.fetchBusinessJobs()
                }

                isRefreshing = false
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = scaffoldPadding.calculateBottomPadding(),
                    start = 15.dp,
                    end = 15.dp
                ),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                itemsIndexed(items=collab.jobs, key = {_, it -> it.id}) { index, job ->
                    SegmentedListItem(
                        shapes = ListItemDefaults.segmentedShapes(index = index, count = collab.jobs.count()),
                        leadingContent = @Composable {
                            AsyncImage(
                                model = if (type == "creator") job.postedBy.profile.profilePhoto
                                else ApiConfig.current.baseURL + job.postedBy.profile.profilePhoto,
                                contentDescription = "profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                        },
                        supportingContent = @Composable {
                            if (type == "creator") {
                                Text("Target category: ${CategoryList.label(job.targetCreator)}")
                            } else {
                                Text("Posted: ${job.timeAgo}")
                            }
                        },
                        overlineContent = {
                            if (type == "creator") {
                                Text(job.postedBy.profile.name)
                            }
                        },
                        onClick = { selectedJob = job },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(job.title)
                    }
                }

                if (collab.jobs.isEmpty()) {
                    item {
                        Empty(
                            type = "collab",
                            message = "No new collaboration available",
                            isLoading = collab.isLoading
                        )
                    }
                }
            }
        }

        selectedJob?.let { job ->
            CloutSheet(
                {selectedJob = null},
            ) {
                if (type == "creator") {
                    CollabDetail(
                        job,
                        onNavigateToQuestions = onNavigateToQuestions,
                        onNavigateToOtherProfile = onNavigateToOtherProfile,
                        onClose = {
                            selectedJob = null
                        }
                    )
                } else {
                    Applications(
                        job = job,
                        onNavigateToOtherProfile = onNavigateToOtherProfile,
                        onNavigateToAnswers = onNavigateToAnswers,
                        onClose = {
                            selectedJob = null
                        }
                    )
                }
            }
        }
    }
}