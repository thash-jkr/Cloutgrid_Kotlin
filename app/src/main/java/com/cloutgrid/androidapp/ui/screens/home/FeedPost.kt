package com.cloutgrid.androidapp.ui.screens.home

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.ui.theme.Second
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import com.cloutgrid.androidapp.ui.components.ReportBox
import com.cloutgrid.androidapp.ui.theme.First
import com.cloutgrid.androidapp.ui.theme.OffWhite
import kotlinx.coroutines.launch

@Composable
fun FeedPost(
    post: PostModel,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onUserClick: (username: String) -> Unit,
    modifier: Modifier = Modifier,
    isOwner: Boolean = false,
    onBlockClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

    var menuExpanded by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun triggerBounce() {
        coroutineScope.launch {
            scaleAnim.snapTo(1f)

            scaleAnim.animateTo(
                targetValue = 1.4f,
                animationSpec = tween(durationMillis = 200)
            )

            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = OffWhite),
        shape = RectangleShape
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = post.author.profilePhoto,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Row {
                    Text(
                        text = post.author.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onUserClick(post.author.username) }
                    )

                    if (post.collaboration != null) {
                        Text(
                            text = " with ",
                            color = Color.Gray
                        )

                        Text(
                            text = post.collaboration.profile.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onUserClick(post.collaboration.profile.username) }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Box {
                    IconButton({
                        menuExpanded = true
                    }) {
                        Icon(
                            Icons.Rounded.MoreHoriz,
                            contentDescription = "More Options",
                            tint = Color.Black
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(Color.White),
                    ) {
                        if (isOwner) {
                            DropdownMenuItem(
                                text = { Text("Delete Post") },
                                onClick = {
                                    showDeleteDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        contentDescription = "Report Icon"
                                    )
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Report Post") },
                                onClick = {
                                    showReportDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Report,
                                        contentDescription = "Report Icon"
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Report @${post.postedBy.profile.username}") },
                                onClick = {
                                    showReportDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Report,
                                        contentDescription = "Report Icon"
                                    )
                                }
                            )

                            if (post.collaboration != null) {
                                DropdownMenuItem(
                                    text = { Text("Report @${post.collaboration.profile.username}") },
                                    onClick = {
                                        showReportDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Rounded.Report,
                                            contentDescription = "Report Icon"
                                        )
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text("Block @${post.postedBy.profile.username}") },
                                onClick = {
                                    showBlockDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Block,
                                        contentDescription = "Report Icon"
                                    )
                                }
                            )
                        }
                    }
                }
            }

            AsyncImage(
                model = post.image,
                contentDescription = "Post Content",
                modifier = Modifier
                    .pointerInput(post.isLiked) {
                        detectTapGestures(
                            onDoubleTap = {
                                triggerBounce()

                                if (!post.isLiked) {
                                    onLikeClick()
                                }
                            }
                        )
                    }
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!post.isLiked) triggerBounce()
                            onLikeClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = if (post.isLiked) "Liked" else "Like",
                        tint = if (post.isLiked) Second else First,
                        modifier = Modifier
                            .scale(scaleAnim.value)
                            .then(Modifier.size(30.dp))
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${post.likeCount} ")
                            }
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append("Likes")
                            }
                        },
                        fontSize = 15.sp
                    )

                    Text(
                        text = " · ",
                        color = Color.Gray,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${post.commentCount} ")
                            }
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append("Comments")
                            }
                        },
                        fontSize = 15.sp
                    )
                }

                IconButton(onClick = onCommentClick) {
                    Icon(
                        imageVector = Icons.Rounded.CropSquare,
                        contentDescription = "Comment Button",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${post.author.username } ")
                        }
                        append(post.caption)
                    },
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false},
            title = { Text("Block user?") },
            text = { Text("Are you sure you want to block this user?") },
            confirmButton = {
                TextButton(
                    onClick = { onBlockClick() }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false},
            title = { Text("Delete post?") },
            text = { Text("Are you sure you want to delete this post?") },
            confirmButton = {
                TextButton(
                    onClick = { onDeleteClick() }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }

    if (showReportDialog) {
        ReportBox(
            title = "Report",
            body = "Tell us why you want to report this. Our team will take appropriate action",
            onDismiss = { showReportDialog = false },
            onSubmit = {
                Toast.makeText(
                    context,
                    "Reported",
                    Toast.LENGTH_SHORT
                ).show()

                showReportDialog = false
            }
        )
    }
}