package com.cloutgrid.androidapp.ui.screens.collab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cloutgrid.androidapp.ui.components.CloutHeader

@Composable
fun CollabScreen() {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Collaborate",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
            ) {

            }
        }
    }
}