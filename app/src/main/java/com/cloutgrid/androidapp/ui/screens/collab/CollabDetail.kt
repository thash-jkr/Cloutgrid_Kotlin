package com.cloutgrid.androidapp.ui.screens.collab

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.data.model.JobModel
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.ReportBox

@Composable
fun CollabDetail(
    job: JobModel,
    onNavigateToQuestions: (Int) -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
    onClose: () -> Unit,
    collab: CollabManager = hiltViewModel()
) {
    var showReportAlert by remember { mutableStateOf(false) }
    var reportContent by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Default.Info,
                        contentDescription = "Report",
                        onClick = { showReportAlert = true }
                    ),
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Posted by:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = job.postedBy.profile.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(onClick = { onNavigateToOtherProfile(job.postedBy.profile.username) }) {
                        Text("Business Profile")
                    }

                    Button(
                        onClick = {
                            if (job.questions.isNotEmpty()) {
                                onClose()
                                onNavigateToQuestions(
                                    job.id
                                )
                            } else {
                                collab.submitApplication(job.id, emptyMap())
                                onClose()
                            }
                        },
                        enabled = !job.isApplied
                    ) {
                        Text(if (job.isApplied) "Applied" else "Apply")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                ) {
                    Text("About the role:", fontWeight = FontWeight.Bold)
                    Text(job.description)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                ) {
                    Text("Requirements:", fontWeight = FontWeight.Bold)

                    job.requirements.split(",").forEach { req ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("•  ")
                            Text(req.trim())
                        }
                    }
                }
            }
        }

        if (showReportAlert) {
            ReportBox(
                title = "Report Content",
                body = "If you think this collaboration posting " +
                        "violated our community guidelines, please report it. " +
                        "Our team will review it shortly",
                onDismiss = {
                    reportContent = ""
                    showReportAlert = false
                },
                onSubmit = {
                    reportContent = it
                    showReportAlert = false
                }
            )
        }
    }
}