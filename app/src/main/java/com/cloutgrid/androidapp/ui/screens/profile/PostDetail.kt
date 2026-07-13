package com.cloutgrid.androidapp.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import com.cloutgrid.androidapp.ui.screens.home.Comments
import com.cloutgrid.androidapp.ui.screens.home.FeedPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetail(
    id: Int,
    onNavigateBack: () -> Unit,
    other: Boolean,
    profile: ProfileManager = hiltViewModel()
) {
    var post = if (other) {
        profile.otherPosts.find { it.id == id }
    } else {
        profile.posts.find { it.id == id }
    }

    if (post == null) {
        post = if (other) {
            profile.otherCollabs.find { it.id == id }
        } else {
            profile.collabs.find { it.id == id }
        }
    }

    var showCommentsSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Post Detail",
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onNavigateBack
                ),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                if (post != null) {
                    FeedPost(
                        post = post,
                        onLikeClick = { profile.likePost(id) },
                        onCommentClick = { showCommentsSheet = true },
                        onUserClick = {},
                        modifier = Modifier,
                        isOwner = !other,
                        onBlockClick = {
                            if (other) {
                                profile.handleBlock(post.postedBy.profile.username, true)
                                onNavigateBack()
                            }
                        },
                        onDeleteClick = {
                            if (!other) {
                                profile.handleDeletePost(post.id)
                                onNavigateBack()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCommentsSheet) {
        CloutSheet(
            onDismiss = { showCommentsSheet = false },
            content = { Comments(
                comments = profile.comments,
                isLoading = profile.isLoading,
                user = profile.user.collectAsState().value,
                onAddComment = { profile.addComment(id, it) },
                onDeleteComment = { profile.deleteComment(id, it) }
            ) }
        )
    }
}