package com.cloutgrid.androidapp.ui.screens.collab

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.cloutgrid.androidapp.models.CategoryList
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import com.cloutgrid.androidapp.ui.components.Empty
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CollabScreen(
    collab: CollabManager = hiltViewModel(),
    scaffoldPadding: PaddingValues,
    onNavigateToQuestions: (Int) -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
) {
    var selectedJob by remember { mutableStateOf<JobModel?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        collab.events.collect { success ->
            if (success) {
                Toast.makeText(context, collab.successMessage, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, collab.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (collab.jobs.isEmpty()) {
            collab.fetchJobs()
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
            isRefreshing = collab.isLoading,
            onRefresh = {
                collab.fetchJobs()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = scaffoldPadding.calculateBottomPadding()
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items=collab.jobs, key = {it.id}) { job ->
                    ListItem(
                        leadingContent = @Composable {
                            AsyncImage(
                                model = job.postedBy.profile.profilePhoto,
                                contentDescription = "profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                        },
                        supportingContent = @Composable {
                            Text(CategoryList.label(job.postedBy.targetAudience ?: ""))
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
                CollabDetail(
                    job,
                    onNavigateToQuestions = onNavigateToQuestions,
                    onNavigateToOtherProfile = onNavigateToOtherProfile,
                    onClose = {
                        selectedJob = null
                    }
                )
            }
        }
    }
}