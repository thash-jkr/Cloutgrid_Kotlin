package com.cloutgrid.androidapp.ui.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader

@Composable
fun OtherProfile(
    username: String,
    onNavigateBack: () -> Boolean
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "@${username}",
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    onClick = { onNavigateBack() }
                ),
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Outlined.PersonAddAlt,
                        contentDescription = "Follow",
                        onClick = {  }
                    ),
                    HeaderAction(
                        icon = Icons.Outlined.Menu,
                        contentDescription = "Options",
                        onClick = { }
                    )
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = { },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                ),
                modifier = Modifier.fillMaxSize()
            ) {

            }
        }
    }
}