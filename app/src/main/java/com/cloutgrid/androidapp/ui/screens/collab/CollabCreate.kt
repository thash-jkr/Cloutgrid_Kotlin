package com.cloutgrid.androidapp.ui.screens.collab

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CategoryList
import com.cloutgrid.androidapp.ui.components.CategorySheet
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import com.cloutgrid.androidapp.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CollabCreate(
    collab: CollabManager = hiltViewModel(),
    onNavigateBack: () -> Boolean
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val titleState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val requirementsState = rememberTextFieldState()
    val questionState = rememberTextFieldState()
    var category by remember { mutableStateOf("") }

    var questions by remember { mutableStateOf(emptyList<String>()) }

    var showCategorySheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        collab.events.collect { success ->
            if (success) {
                Toast.makeText(context, collab.successMessage, Toast.LENGTH_LONG).show()
                onNavigateBack()
            } else {
                Toast.makeText(context, collab.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CloutHeader(
                title = "Create Collab",
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    onClick = { onNavigateBack() }
                )
            )
        }
    ) { innerPadding ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 15.dp,
                        end = 15.dp,
                        bottom = 100.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                OutlinedTextField(
                    state = titleState,
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        Text("A short, clear title for the collaboration")
                    }
                )

                OutlinedTextField(
                    state = descriptionState,
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        Text("Describe what the collaboration involves and what you're looking for")
                    }
                )

                OutlinedTextField(
                    state = requirementsState,
                    label = { Text("Requirements") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        Text("Separate each requirement with a comma")
                    }
                )

                OutlinedTextField(
                    state = questionState,
                    label = { Text("Questions") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    supportingText = {
                        Text("Optional — add questions for creators to answer when applying." +
                                "Enter a question then press the + button to add it." +
                                "You can add multiple questions")
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add question",
                            modifier = Modifier.clickable {
                                if (questionState.text.toString().isNotEmpty()) {
                                    questions = questions + questionState.text.toString()
                                    questionState.clearText()
                                } else {
                                    Toast.makeText(context, "Question cannot be empty", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    questions.forEachIndexed { index, question ->
                        key(question) { // manual keying, since forEach has no built-in key param
                            SegmentedListItem(
                                shapes = ListItemDefaults.segmentedShapes(index = index, count = questions.count()),
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.Cancel,
                                        contentDescription = "Remove question",
                                        modifier = Modifier.clickable(
                                            onClick = {
                                                questions = questions.filterIndexed { i, _ -> i != index }
                                            }
                                        )
                                    )
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = OffWhite,
                                ),
                            ) {
                                Text("${index + 1}. $question")
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showCategorySheet = true
                        }
                ) {
                    OutlinedTextField(
                        value = CategoryList.label(category),
                        onValueChange = {},
                        label = { Text("Target Creator") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (collab.isLoading) {
                        LoadingIndicator()
                    } else {
                        Button({
                            collab.createJob(
                                title = titleState.text.toString(),
                                description = descriptionState.text.toString(),
                                requirements = requirementsState.text.toString(),
                                targetCreator = category,
                                questions = questions
                            )
                        }) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }

    if (showCategorySheet) {
        CloutSheet(
            { showCategorySheet = false }
        ) {
            CategorySheet(CategoryList.allOptions, category) { cat ->
                category = cat.value
                showCategorySheet = false
            }
        }
    }
}