package com.cloutgrid.androidapp.data.model

import android.text.format.DateUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class NotificationModel(
    val id: Int,
    val message: String,
    @SerialName("is_read") val isRead: Boolean
)

@Serializable
data class PostModel(
    val id: Int,
    val author: UserProfile,
    @SerialName("posted_by") val postedBy: UserContainer,
    val collaboration: UserContainer? = null,
    @SerialName("like_count") var likeCount: Int,
    @SerialName("comment_count") var commentCount: Int,
    @SerialName("is_liked") var isLiked: Boolean,
    val image: String,
    val caption: String
)

@Serializable
data class CommentModel(
    val id: Int,
    val user: UserProfile,
    val content: String,
    @SerialName("commented_at") val commentedAt: String
) {
    // Replaces Swift's computed property using a custom Kotlin getter
    val timeAgo: String
        get() {
            return try {
                // Parses standard ISO8601 internet date strings automatically
                val commentMillis = Instant.parse(commentedAt).toEpochMilli()
                val nowMillis = Instant.now().toEpochMilli()

                // Matches Swift's abbreviated RelativeDateTimeFormatter (e.g. "2h ago", "45m ago")
                DateUtils.getRelativeTimeSpanString(
                    commentMillis,
                    nowMillis,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL
                ).toString()
            } catch (e: Exception) {
                "Just now"
            }
        }
}

@Serializable
data class LikeResponse(
    val liked: Boolean,
    @SerialName("like_count") val likeCount: Int
)

@Serializable
data class PostResponse(
    val results: List<PostModel>,
    val next: String? = null
)