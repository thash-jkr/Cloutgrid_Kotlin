package com.cloutgrid.androidapp.data.repository

import com.cloutgrid.androidapp.data.model.ApplicationModel
import com.cloutgrid.androidapp.data.model.JobModel
import com.cloutgrid.androidapp.data.network.APIService
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollabRepository @Inject constructor(
    private val apiService: APIService
) {
    suspend fun fetchJobs(): List<JobModel> {
        return apiService.request(
            endpoint = "/jobs/",
            method = "GET",
            requireAuth = true
        )
    }

    suspend fun fetchBusinessJobs(): List<JobModel> {
        return apiService.request(
            endpoint = "/jobs/my-jobs/",
            method = "GET",
            requireAuth = true
        )
    }

    suspend fun fetchApplications(jobId: Int): List<ApplicationModel> {
        return apiService.request(
            endpoint = "/jobs/my-jobs/$jobId/",
            method = "GET",
            requireAuth = true
        )
    }

    suspend fun createJob(
        title: String,
        description: String,
        requirements: String,
        targetCreator: String,
        questions: List<String>
    ) {
        val params = mapOf(
            "title" to title,
            "description" to description,
            "requirements" to requirements,
            "target_creator" to targetCreator,
            "questions" to Json.encodeToString(questions)
        )

        apiService.multipartRequest<Unit>(
            endpoint = "/jobs/",
            method = "POST",
            imageBytes = null,
            imageKey = null,
            params = params,
            requireAuth = true
        )
    }

    suspend fun deleteJob(id: Int) {
        apiService.request<Unit>(
            endpoint = "/jobs/$id/",
            method = "DELETE",
            requireAuth = true
        )
    }

    suspend fun submitApplication(id: Int, answers: Map<Int, String>) {
        val stringKeyedAnswers = answers.mapKeys { it.key.toString() }

        apiService.request<Unit>(
            endpoint = "/jobs/$id/apply/",
            method = "POST",
            body = mapOf("answers" to stringKeyedAnswers),
            requireAuth = true
        )
    }
}