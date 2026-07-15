package com.cloutgrid.androidapp.ui.screens.collab

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.ReportBox
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Questions(
    id: Int,
    onNavigateBack: () -> Boolean,
    collab: CollabManager = hiltViewModel()
) {
    var showReportAlert by remember { mutableStateOf(false) }

    val job = collab.jobs.firstOrNull { it.id == id }
    val questions = job?.questions ?: emptyList()

    val textFieldStates = remember(questions) {
        questions.associate { it.id to TextFieldState() }
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        collab.events.collect { success ->
            if (success) {
                Toast.makeText(context, collab.successMessage, Toast.LENGTH_LONG).show()
                delay(500.milliseconds)
                onNavigateBack()
            } else {
                Toast.makeText(context, collab.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    onClick = { onNavigateBack() }
                ),
                title = "Questions",
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
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp,
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                itemsIndexed(questions) { index, question ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${index + 1}. ${question.content}")

                        OutlinedTextField(
                            state = textFieldStates.getValue(question.id),
                            label = { Text("Your answer") },
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 20.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            val answers: Map<Int, String> = textFieldStates.mapValues { (_, state) ->
                                state.text.toString()
                            }

                            val hasEmptyAnswer = answers.values.any { it.isBlank() }

                            if (hasEmptyAnswer) {
                                Toast.makeText(
                                    context,
                                    "Please answer all questions",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                collab.submitApplication(id, answers)
                            }
                        }) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }

    if (showReportAlert) {
        ReportBox(
            title = "Report Content",
            body = "Let us know what you think should be reported. Our team will review it shortly",
            onDismiss = { showReportAlert = false },
            onSubmit = {
                showReportAlert = false
            }
        )
    }
}