package com.cloutgrid.androidapp.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.Empty
import com.cloutgrid.androidapp.ui.screens.integration.Instagram
import com.cloutgrid.androidapp.ui.screens.integration.InstagramConstants
import com.cloutgrid.androidapp.ui.screens.integration.YouTube
import com.cloutgrid.androidapp.ui.screens.integration.YoutubeConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: ProfileManager = hiltViewModel(),
    scaffoldPadding: PaddingValues,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPostDetail: (PostModel, Boolean) -> Unit
) {
    val user by profile.user.collectAsState()

    var selectedTab by remember { mutableStateOf("Posts") }

    val tabs = remember(user) {
        if (user?.profile?.userType == "creator") {
            listOf("Posts", "Instagram", "YouTube")
        } else {
            listOf("Posts", "Collabs")
        }
    }

    LaunchedEffect(user) {
        if (user != null && profile.posts.isEmpty()) {
            profile.fetchPosts(user?.profile?.username ?: "", false)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "@${user?.profile?.username}",
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Outlined.EditNote,
                        contentDescription = "Edit",
                        onClick = { onNavigateToEditProfile() }
                    ),
                    HeaderAction(
                        icon = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        onClick = { onNavigateToSettings() }
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
                    bottom = scaffoldPadding.calculateBottomPadding()
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                if (user != null) {
                    item {
                        ProfileHeader(user = user!!)
                    }
                }

                item {
                    ProfileSelector(
                        tabs,
                        selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                when(selectedTab) {
                    "Posts" -> {
                        if (profile.posts.isEmpty()) {
                            item {
                                Empty(
                                    type = "post",
                                    message = "No posts found",
                                    isLoading = profile.isLoading
                                )
                            }
                        } else {
                            postGridItems(
                                posts = profile.posts,
                                onPostClick = { post -> onNavigateToPostDetail(post, false) }
                            )
                        }
                    }
                    "Instagram" -> {
                        item {
                            Instagram()
                        }
                    }
                    "YouTube" -> {
                        item {
                            YouTube()
                        }
                    }
                    "Collabs" -> {
                        if (profile.posts.isEmpty()) {
                            item {
                                Empty(
                                    type = "post",
                                    message = "No collab posts found",
                                    isLoading = profile.isLoading
                                )
                            }
                        } else {
                            postGridItems(
                                posts = profile.collabs,
                                onPostClick = { post -> onNavigateToPostDetail(post, false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun LazyListScope.postGridItems(
    posts: List<PostModel>,
    onPostClick: (PostModel) -> Unit = {}
) {
    val chunkedRows = posts.chunked(3)

    items(items = chunkedRows) { rowItems ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (i in 0 until 3) {
                val post = rowItems.getOrNull(i)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    if (post != null) {
                        AsyncImage(
                            model = post.image,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onPostClick(post) }
                        )
                    }
                }
            }
        }
    }
}