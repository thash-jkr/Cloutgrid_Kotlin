package com.cloutgrid.androidapp.ui.screens.messaging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.components.CategoryList
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.Empty
import com.cloutgrid.androidapp.ui.theme.OffWhite
import com.cloutgrid.androidapp.ui.theme.Second

@Composable
fun ChatScreen(
    chat: ChatManager = hiltViewModel(),
    onNavigateBack: () -> Boolean
) {
    val conversations = chat.chats

    LaunchedEffect(conversations) {
        if (conversations.isEmpty()) {
            chat.fetchConversations()
        }
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CloutHeader(
                title = "Chats",
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back button",
                    onClick = {
                        onNavigateBack()
                    },
                )
            )
        }
    ) { innerPadding ->
        Box(
            Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 15.dp,
                    top = innerPadding.calculateTopPadding(),
                    end = 15.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                if (conversations.isEmpty()) {
                    item {
                        Empty(
                            type = "comment",
                            message = "No chats yet",
                            isLoading = chat.isLoading
                        )
                    }
                }

                itemsIndexed(conversations) { index, conversation ->
                    SegmentedListItem(
                        shapes = ListItemDefaults.segmentedShapes(
                            index = index, count = conversations.count()
                        ),
                        colors = ListItemDefaults.colors(
                            containerColor = Color.White,
                            selectedContainerColor = Second
                        ),
                        leadingContent = {
                            AsyncImage(
                                model = ApiConfig.current.baseURL + conversation.user.profile.profilePhoto,
                                contentDescription = "profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                        },
                        supportingContent = {
                            Text(
                                CategoryList.label(
                                    conversation.user.area ?: conversation.user.targetAudience ?: "")
                            )
                        },
                        onClick = {

                        }
                    ) {
                        Text(
                            conversation.user.profile.name
                        )
                    }
                }
            }
        }
    }
}