package com.cloutgrid.androidapp.ui.screens.create

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreatePost(
    selectedImage: Uri,
    onNavigateBack: () -> Unit,
    create: CreateManager = hiltViewModel()
) {
    var caption by remember { mutableStateOf("") }
    val collabState = remember {
        TextFieldState("")
    }

    var showCollabSheet by remember { mutableStateOf(false) }

    val user by create.user.collectAsState()
    var isCreator by remember(user) { mutableStateOf(user?.profile?.userType == "creator") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onNavigateBack
                ),
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Outlined.Save,
                        contentDescription = "Save",
                        onClick = {  }
                    ),
                )
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = selectedImage,
                contentDescription = "Loaded image from URI",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp),
                maxLines = 5
            )

            if (isCreator) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showCollabSheet = true
                        }
                ) {
                    OutlinedTextField(
                        state = collabState,
                        label = { Text("Collaboration") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CollabSheet() {
    var query by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Search Collaboration",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding()
                )
            ) { }
        }
    }
}