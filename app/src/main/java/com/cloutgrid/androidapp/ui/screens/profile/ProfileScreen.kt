package com.cloutgrid.androidapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.screens.integration.InstagramConstants
import com.cloutgrid.androidapp.ui.screens.integration.YoutubeConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(profile: ProfileManager = hiltViewModel(), scaffoldPadding: PaddingValues) {
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
        profile.fetchPosts(user?.profile?.username ?: "", false)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "@${user?.profile?.username}",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = {  },
                        modifier = Modifier.size(45.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Notifications",
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    FilledTonalIconButton(
                        onClick = {  },
                        modifier = Modifier.size(45.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Direct Messages",
                            modifier = Modifier.size(25.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = Color.Black
                ),
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
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

//                stickyHeader(key = "profile_tabs") {
//                    TabRow(
//                        selectedTabIndex = selectedTabIndex,
//                        containerColor = MaterialTheme.colorScheme.surface, // Matches background
//                        contentColor = MaterialTheme.colorScheme.primary
//                    ) {
//                        tabs.forEachIndexed { index, title ->
//                            Tab(
//                                selected = selectedTabIndex == index,
//                                onClick = { selectedTabIndex = index },
//                                text = {
//                                    Text(
//                                        text = title,
//                                        fontSize = 14.sp,
//                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
//                                    )
//                                }
//                            )
//                        }
//                    }
//                }
//
                item {
                    ProfileCapsuleSelector(
                        tabs,
                        selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                when(selectedTab) {
                    "Posts" -> {
                        postGridItems(
                            posts = profile.posts,
                            onPostClick = { }
                        )
                    }
                    "Instagram" -> {
                        item {
                            InstagramConstants()
                        }
                    }
                    "YouTube" -> {
                        item {
                            YoutubeConstants()
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