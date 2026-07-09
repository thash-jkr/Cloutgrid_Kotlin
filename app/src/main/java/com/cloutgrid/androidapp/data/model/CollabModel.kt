package com.cloutgrid.androidapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("target_creator") val targetCreator: String
)

@Serializable
data class ApplicationModel(
    val id: Int,
    val creator: UserContainer,
    val job: JobModel,
    val answers: List<AnswerModel>
)