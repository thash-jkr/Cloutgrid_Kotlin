package com.cloutgrid.androidapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.R
import com.cloutgrid.androidapp.ui.components.LoadingSpinner
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    home: HomeManager = hiltViewModel(),
    scaffoldPadding: PaddingValues,
    onNavigateToChatScreen: () -> Unit
) {
    var showNotificationSheet by remember { mutableStateOf(false) }
    val notificationSheetState = rememberModalBottomSheetState()

    var selectedPostId by remember { mutableStateOf<Int?>(null) }
    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Cloutgrid Logo",
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                    )
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = { showNotificationSheet = true },
                        modifier = Modifier.size(45.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    FilledTonalIconButton(
                        onClick = { onNavigateToChatScreen() },
                        modifier = Modifier.size(45.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Direct Messages",
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 1f),
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.0f)
                        )
                    )
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
                        onCommentClick = { selectedPostId = post.id },
                        onUserClick = {  }
                    )
                }

                if (home.posts.isNotEmpty() && home.isLoading) {
                    item {
                        LoadingSpinner()
                    }
                }
            }
        }

        if (showNotificationSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showNotificationSheet = false
                },
                sheetState = notificationSheetState,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
                dragHandle = null
            ) {
                Notifications()
            }
        }

        selectedPostId?.let { postId ->
            ModalBottomSheet(
                onDismissRequest = {
                    selectedPostId = null
                },
                sheetState = commentSheetState,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
                dragHandle = null
            ) {
                Comments(
                    postID = postId,
                    homeManager = home,
                    user = user
                )
            }
        }
    }
}