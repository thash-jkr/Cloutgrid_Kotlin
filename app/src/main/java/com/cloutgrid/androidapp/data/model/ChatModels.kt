package com.cloutgrid.androidapp.data.model

import android.text.format.DateUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ConversationModel(
    val id: String,
    val user: UserContainer,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class MessageModel(
    val id: String,
    val sender: UserContainer,
    val content: String,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("created_at") val createdAt: String
) {
    val timeAgo: String
        get() {
            return try {
                val messageMillis = Instant.parse(createdAt).toEpochMilli()
                val nowMillis = Instant.now().toEpochMilli()

                DateUtils.getRelativeTimeSpanString(
                    messageMillis,
                    nowMillis,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL
                ).toString()
            } catch (_: Exception) {
                "Just now"
            }
        }
}

@Serializable
data class ChatRoute(
    val id: String,
    val user: UserContainer
)