package com.cloutgrid.androidapp.data.model

import android.text.format.DateUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class QuestionModel(
    val id: Int,
    val content: String,
    val job: Int
)

@Serializable
data class AnswerModel(
    val id: Int,
    val content: String,
    val application: Int,
    val question: Int
)

@Serializable
data class JobModel(
    val id: Int,
    @SerialName("posted_by") val postedBy: UserContainer,
    val questions: List<QuestionModel>,
    @SerialName("is_applied") val isApplied: Boolean,
    val title: String,
    val description: String,
    val requirements: String,
    @SerialName("target_creator") val targetCreator: String,
    @SerialName("created_at") val createdAt: String
) {
    val timeAgo: String
        get() {
            return try {
                val commentMillis = Instant.parse(createdAt).toEpochMilli()
                val nowMillis = Instant.now().toEpochMilli()

                DateUtils.getRelativeTimeSpanString(
                    commentMillis,
                    nowMillis,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL
                ).toString()
            } catch (_: Exception) {
                "Just now"
            }
        }

    val date: String
        get() {
            return try {
                val instant = Instant.parse(createdAt)
                val formatter = DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .withZone(ZoneId.systemDefault())

                formatter.format(instant)
            } catch (_: Exception) {
                ""
            }
        }
}

@Serializable
data class ApplicationModel(
    val id: Int,
    val creator: UserContainer,
    val job: JobModel,
    val answers: List<AnswerModel>
)