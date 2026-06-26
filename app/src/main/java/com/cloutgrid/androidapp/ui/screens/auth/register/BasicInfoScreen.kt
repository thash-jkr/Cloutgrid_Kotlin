package com.cloutgrid.androidapp.ui.screens.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoScreen(
    type: String,
    onNavigateToMoreInfo: (type: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text="$type Registration", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
            )
        }
    ) {
        innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all=10.dp))

                OutlinedTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all=10.dp))

                OutlinedTextField(
                    state = rememberTextFieldState(),
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all=10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button({onNavigateToMoreInfo(type)}) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicInfoScreenPreview() {
    BasicInfoScreen("Creator", {}, {})
}