package com.cloutgrid.androidapp.ui.screens.collab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.ReportBox

@Composable
fun Answers(
    id: Int,
    onNavigateBack: () -> Boolean,
    collab: CollabManager = hiltViewModel(),
    onNavigateToOtherProfile: (String) -> Unit
) {
    var showReportAlert by remember { mutableStateOf(false) }

    val application = collab.applications.firstOrNull { it.id == id }
    val questions = application?.job?.questions
    val answers = application?.answers

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    onClick = { onNavigateBack() }
                ),
                title = "Answers",
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Default.Info,
                        contentDescription = "Report",
                        onClick = { showReportAlert = true }
                    ),
                )
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp,
                )
            ) {
                if (answers != null) {
                    itemsIndexed(answers, key = { _, answer -> answer.id }) { index, answer ->
                        val question = questions?.firstOrNull { it.id == answer.question }

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${index + 1}. ${question?.content ?: "Question unavailable"}")

                            OutlinedTextField(
                                value = answer.content,
                                onValueChange = {},
                                label = { Text("Answer") },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            HorizontalDivider(
                                modifier = Modifier.padding(bottom = 20.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button({
                            if (application != null) {
                                onNavigateToOtherProfile(application.creator.profile.username)
                            }
                        }) {
                            Text("View Profile")
                        }
                    }
                }
            }
        }
    }

    if (showReportAlert) {
        ReportBox(
            title = "Report Content",
            body = "If you think the user's response violate our community guidelines, " +
                    "please report it. Our team will review it shortly",
            onDismiss = { showReportAlert = false },
            onSubmit = {
                showReportAlert = false
            }
        )
    }
}