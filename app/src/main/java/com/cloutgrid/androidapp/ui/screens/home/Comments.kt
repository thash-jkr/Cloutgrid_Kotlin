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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.CommentModel
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.Empty
import com.cloutgrid.androidapp.ui.components.ReportBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comments(
    comments: List<CommentModel>,
    isLoading: Boolean,
    user: UserContainer?,
    onAddComment: (String) -> Unit,
    onDeleteComment: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showReportAlert by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var reportContent by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(title = "Comments")
        },
        bottomBar = {
            CommentInputBar(
                user = user,
                commentText = commentText,
                onCommentChange = { commentText = it },
                onSendClick = {
                    if (commentText.isNotBlank()) {
                        onAddComment(commentText)
                        commentText = "" // Safely clears input after sending
                    }
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (comments.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 0.dp,
                        top = innerPadding.calculateTopPadding(),
                        end = 0.dp,
                        bottom = innerPadding.calculateBottomPadding() + 16.dp
                    )
                ) {
                    items(
                        items = comments,
                        key = { it.id }
                    ) { commentItem ->
                        CommentRow(
                            comment = commentItem,
                            user = user,
                            onDelete = { onDeleteComment(commentItem.id) },
                            onReport = { showReportAlert = true }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Empty(
                        type = "comment",
                        message = "No comments yet!",
                        isLoading = isLoading,
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                    )
                }
            }
        }
    }

    if (showReportAlert) {
        ReportBox(
            title = "Report Content",
            body = "Let us know what you think should be reported. Our team will review it shortly",
            onDismiss = {
                reportContent = ""
                showReportAlert = false
            },
            onSubmit = {
                reportContent = it
                showReportAlert = false
            }
        )
    }
}

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
                .background(Color.White),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = ApiConfig.current.baseURL + comment.user.profilePhoto,
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "${comment.user.name} • ${comment.timeAgo}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = comment.content,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = TextStyle(
                            lineHeight = 1.2.em
                        )
                    )
                }
            }
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
        }
    }
}

@Composable
private fun CommentInputBar(
    user: UserContainer?,
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp))
            .background(Color.White, shape = RoundedCornerShape(28.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
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
                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
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

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(
            onClick = onSendClick,
            enabled = commentText.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send Comment",
                tint = if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}