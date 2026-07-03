package com.cloutgrid.androidapp.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@Composable
fun FeedPost(
    post: PostModel,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onUserClick: (username: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLiked = post.isLiked

    val iconModifier = if (isLiked) {
        Modifier
            .size(25.dp)
            .background(color = Second, shape = CircleShape)
            .padding(3.dp)
    } else {
        Modifier
            .size(25.dp)
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .padding(3.dp)
    }

    val iconColor = if (isLiked) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val coroutineScope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RectangleShape
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.author.profilePhoto,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(25.dp)
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
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = if (post.isLiked) "Liked" else "Like",
                        tint = iconColor,
                        modifier = Modifier
                            .scale(scaleAnim.value)
                            .then(iconModifier)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${post.likeCount} hits",
                        fontSize = 15.sp
                    )

                    Text(
                        text = " · ",
                        color = Color.Gray,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${post.commentCount} comments",
                        fontSize = 15.sp
                    )
                }

                IconButton(onClick = onCommentClick) {
                    Icon(
                        imageVector = Icons.Outlined.CropSquare,
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
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}