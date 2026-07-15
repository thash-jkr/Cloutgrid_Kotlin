package com.cloutgrid.androidapp.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.R
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import com.cloutgrid.androidapp.ui.shared.TabItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    home: HomeManager = hiltViewModel(),
    scaffoldPadding: PaddingValues,
    onNavigateToChatScreen: () -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
    onSelectTab: (TabItem) -> Unit,
    onNavigateToTest: () -> Unit,
) {
    var showNotificationSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<Int?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val feedState = rememberLazyListState()
    val pagination by remember {
        derivedStateOf {
            val layoutInfo = feedState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount

            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleItemIndex >= (totalItemsNumber - 3)
                    && !home.isLoading
                    && home.nextCursor != null
        }
    }

    LaunchedEffect(Unit) {
        if (home.posts.isEmpty()) {
            home.fetchPosts(true)
        }
        if (home.notifications.isEmpty()) {
            home.fetchNotifications()
        }
    }

    LaunchedEffect(pagination) {
        if (pagination) {
            home.fetchPosts(false)
        }
    }

    val posts = home.posts
    val user by home.user.collectAsState()
    val appLogoVector = ImageVector.vectorResource(id = R.drawable.cloutgrid_logo)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                icon = HeaderAction(
                    icon = appLogoVector,
                    contentDescription = "Logo",
                    onClick = {}
                ),
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Rounded.NotificationsNone,
                        contentDescription = "Notifications",
                        onClick = { showNotificationSheet = true }
                    ),
                    HeaderAction(
                        icon = Icons.Rounded.ChatBubbleOutline,
                        contentDescription = "Direct Messages",
                        onClick = { onNavigateToChatScreen() }
                    ),
                    HeaderAction(
                        icon = Icons.Rounded.SettingsBackupRestore,
                        contentDescription = "Test",
                        onClick = { onNavigateToTest() }
                    ),
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true

                    home.fetchPosts(true)
                    home.fetchNotifications()

                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            if (home.posts.isEmpty() && home.isLoading) {
                Column(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                ) {
                    FeedLoading()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = scaffoldPadding.calculateBottomPadding()
                    ),
                    state = feedState
                ) {
                    items(
                        items = posts,
                        key = { it.id },
                    ) { post ->
                        FeedPost(
                            post = post,
                            onLikeClick = { home.likePost(post.id) },
                            onCommentClick = {
                                home.fetchComments(post.id)
                                selectedPostId = post.id
                            },
                            onUserClick = {
                                    username ->
                                run {
                                    if (username == user?.profile?.username) {
                                        onSelectTab(TabItem.Profile)
                                    } else {
                                        onNavigateToOtherProfile(username)
                                    }
                                }
                            },
                            onBlockClick = {
                                home.handleBlock(
                                    post.postedBy.profile.username,
                                    true
                                )
                            },
                            isOwner = post.postedBy.profile.username == user?.profile?.username,
                            onDeleteClick = {
                                home.deletePost(post.id)
                            }
                        )
                    }

                    if (home.posts.isNotEmpty() && home.isLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingIndicator()
                            }
                        }
                    }
                }
            }
        }

        if (showNotificationSheet) {
            CloutSheet(
                { showNotificationSheet = false },
                { Notifications() }
            )
        }

        selectedPostId?.let { postId ->
            CloutSheet(
                { selectedPostId = null },
                { Comments(
                    comments = home.comments,
                    isLoading = home.isLoading,
                    user = user,
                    onAddComment = { home.addComment(postId, it) },
                    onDeleteComment = { home.deleteComment(postId, it) }
                ) }
            )
        }
    }
}