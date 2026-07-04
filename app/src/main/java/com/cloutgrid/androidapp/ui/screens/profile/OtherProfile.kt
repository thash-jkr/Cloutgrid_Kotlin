package com.cloutgrid.androidapp.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.PersonRemoveAlt1
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.R
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.data.model.MenuAction
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.ReportBox
import com.cloutgrid.androidapp.ui.screens.integration.InstagramConstants
import com.cloutgrid.androidapp.ui.screens.integration.YoutubeConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherProfile(
    username: String,
    onNavigateBack: () -> Boolean,
    profile: ProfileManager = hiltViewModel(),
    onNavigateToPostDetail: (PostModel, Boolean) -> Unit
) {
    LaunchedEffect(username) {
        if (profile.otherProfile == null) {
            profile.fetchProfile(username, true)
        }
    }

    val otherProfile = profile.otherProfile
    var selectedTab by remember { mutableStateOf("Posts") }
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showUnfollowDialog by remember { mutableStateOf(false) }

    LaunchedEffect(otherProfile) {
        if (otherProfile != null) {
            if (profile.otherPosts.isEmpty()) {
                profile.fetchPosts(username, true)
            }

            if (profile.otherCollabs.isEmpty() && otherProfile.profile.userType == "business") {
                profile.fetchCollabs(username, true)
            }
        }
    }

    val tabs = remember(otherProfile) {
        if (otherProfile?.profile?.userType == "creator") {
            listOf("Posts", "Instagram", "YouTube")
        } else {
            listOf("Posts", "Collabs")
        }
    }

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
                        icon = if (otherProfile == null) {
                            Icons.Outlined.Person
                        } else if (otherProfile.isFollowing == true) {
                            Icons.Outlined.PersonRemoveAlt1
                        } else {
                            Icons.Outlined.PersonAddAlt
                        },
                        contentDescription = "Follow",
                        onClick = {
                            if (otherProfile?.isFollowing == true) {
                                showUnfollowDialog = true
                            } else {
                                profile.handleFollow(username, true)

                                Toast.makeText(
                                    profile.context,
                                    "You have followed @$username",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ),
                    HeaderAction(
                        icon = Icons.Outlined.Menu,
                        contentDescription = "Options",
                        onClick = {  },
                        menuItems = listOf(
                            MenuAction(
                                title = "Block @${username}",
                                icon = Icons.Outlined.PersonRemoveAlt1,
                                onClick = {
                                    showBlockDialog = true
                                }
                            ),
                            MenuAction(
                                title = "Report @${username}",
                                icon = Icons.Outlined.Report,
                                onClick = { showReportDialog = true }
                            )
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
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                if (otherProfile != null) {
                    if (otherProfile.isBlocker == true || otherProfile.isBlocking == true) {
                        item {
                            BlockedProfile(
                                otherProfile.isBlocker == true,
                                onButtonClick = { profile.handleBlock(username, false) }
                            )
                        }
                    } else {
                        item {
                            ProfileHeader(otherProfile)
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
                                postGridItems(
                                    posts = profile.otherPosts,
                                    onPostClick = { post -> onNavigateToPostDetail(post, true) }
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
                            "Collabs" -> {
                                postGridItems(
                                    posts = profile.otherCollabs,
                                    onPostClick = { post -> onNavigateToPostDetail(post, true) }
                                )
                            }
                        }
                    }
                }
            }

            if (showReportDialog) {
                ReportBox(
                    title = "Report @${username}?",
                    body = "Tell us why you want to report this user. Our team will take appropriate action",
                    onDismiss = { showReportDialog = false },
                    onSubmit = {
                        Toast.makeText(
                            profile.context,
                            "User reported",
                            Toast.LENGTH_SHORT
                        ).show()

                        showReportDialog = false
                    }
                )
            }

            if (showBlockDialog) {
                AlertDialog(
                    onDismissRequest = { showBlockDialog = false},
                    title = { Text("Block @${username}?") },
                    text = { Text("Are you sure you want to block this user?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                profile.handleBlock(username, true)
                                showBlockDialog = false
                            }
                        ) {
                            Text("Block")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBlockDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = Color.White
                )
            }

            if (showUnfollowDialog) {
                AlertDialog(
                    onDismissRequest = { showUnfollowDialog = false},
                    title = { Text("Unfollow @${username}?") },
                    text = { Text("Are you sure you want to unfollow this user?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                profile.handleFollow(username, false)
                                showUnfollowDialog = false

                                Toast.makeText(
                                    profile.context,
                                    "You have unfollowed @$username",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Text("Unfollow")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showUnfollowDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun BlockedProfile(
    isBlocker: Boolean,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isBlocker)
            "You cannot view this profile!"
        else
            "You have blocked this profile!"
        )

        Image(
            painter = painterResource(id = R.drawable.blocked),
            contentDescription = "Empty State Graphic",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(150.dp)
                .padding(10.dp)
        )

        if (!isBlocker) {
            Button(onButtonClick) {
                Text("Unblock")
            }
        }
    }
}