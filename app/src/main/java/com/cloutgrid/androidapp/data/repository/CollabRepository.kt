package com.cloutgrid.androidapp.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.cloutgrid.androidapp.data.model.ApplicationModel
import com.cloutgrid.androidapp.data.model.EmptyResponse
import com.cloutgrid.androidapp.data.model.JobModel
import com.cloutgrid.androidapp.data.network.APIService
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollabRepository @Inject constructor(
    private val apiService: APIService
) {
    val jobs = mutableStateListOf<JobModel>()
    val applications = mutableStateListOf<ApplicationModel>()

    suspend fun fetchJobs() {
        val response: List<JobModel> = apiService.request(
            endpoint = "/jobs/",
            method = "GET",
            requireAuth = true
        )

        jobs.clear()
        jobs.addAll(response)
    }

    suspend fun fetchBusinessJobs() {
        val response: List<JobModel> = apiService.request(
            endpoint = "/jobs/my-jobs/",
            method = "GET",
            requireAuth = true
        )

        jobs.clear()
        jobs.addAll(response)
    }

    suspend fun fetchApplications(jobId: Int) {
        val response: List<ApplicationModel> = apiService.request(
            endpoint = "/jobs/my-jobs/$jobId/",
            method = "GET",
            requireAuth = true
        )

        applications.clear()
        applications.addAll(response)
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
        apiService.request<EmptyResponse>(
            endpoint = "/jobs/$id/",
            method = "DELETE",
            requireAuth = true
        )

        jobs.removeAll { it.id == id }
        applications.clear()
    }

    suspend fun submitApplication(id: Int, answers: Map<Int, String>) {
        val stringKeyedAnswers = answers.mapKeys { it.key.toString() }

        apiService.request<EmptyResponse>(
            endpoint = "/jobs/$id/apply/",
            method = "POST",
            body = mapOf("answers" to stringKeyedAnswers),
            requireAuth = true
        )
    }
}