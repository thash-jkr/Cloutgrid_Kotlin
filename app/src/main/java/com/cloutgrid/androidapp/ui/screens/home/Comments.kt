package com.cloutgrid.androidapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.CommentModel
import kotlinx.coroutines.launch
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.components.Empty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comments(
    postID: Int,
    homeManager: HomeManager,
    user: UserContainer?,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var showReportAlert by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var reportContent by remember { mutableStateOf("") }

    LaunchedEffect(postID) {
        homeManager.fetchComments(postID = postID)
    }

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Comments",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )

                CommentInputBar(
                    user = user,
                    commentText = commentText,
                    onCommentChange = { commentText = it },
                    onSendClick = {
                        coroutineScope.launch {
                            homeManager.addComment(postID = postID, content = commentText)
                            commentText = ""
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (homeManager.comments.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = homeManager.comments,
                        key = { it.id }
                    ) { commentItem ->
                        CommentRow(
                            comment = commentItem,
                            user = user,
                            onDelete = {
                                coroutineScope.launch {
                                    homeManager.deleteComment(postID = postID, commentID = commentItem.id)
                                }
                            },
                            onReport = {
                                showReportAlert = true
                            }
                        )
                    }
                }
            } else {
                Empty(type = "comment", message = "No comments yet!", isLoading = homeManager.isLoading)
            }
        }
    }

    if (showReportAlert) {
        AlertDialog(
            onDismissRequest = { showReportAlert = false },
            title = { Text("Report Content") },
            text = {
                Column {
                    Text(
                        text = "Please tell us why you are reporting this. Our team will review it shortly.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = reportContent,
                        onValueChange = { reportContent = it },
                        placeholder = { Text("Reason (e.g., Spam, Harassment)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showReportAlert = false }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        reportContent = ""
                        showReportAlert = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

// --- Sub-Component: Single Comment List Item Row ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentRow(
    comment: CommentModel,
    user: UserContainer?,
    onDelete: () -> Unit,
    onReport: () -> Unit
) {
    val isCurrentUser = comment.user.username == user?.profile?.username

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                if (isCurrentUser) onDelete() else onReport()
                false
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = if (isCurrentUser) Icons.Default.Delete else Icons.Default.Warning,
                    contentDescription = if (isCurrentUser) "Delete" else "Report",
                    tint = Color.White
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface) // 🌟 Shields the red background completely
        ) {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Removed the background modifier from here since the parent Column handles it now
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ApiConfig.current.baseURL + comment.user.profilePhoto,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${comment.user.name} • ${comment.timeAgo}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comment.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentInputBar(
    user: UserContainer?,
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ApiConfig.current.baseURL + (user?.profile?.profilePhoto),
                contentDescription = "My Profile Photo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = commentText,
                onValueChange = onCommentChange,
                textStyle = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(1.dp, Color.Black.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (commentText.isEmpty()) {
                            Text(
                                text = "Add comment",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSendClick,
                enabled = commentText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Comment",
                    tint = if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}