package com.cloutgrid.androidapp.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.APIService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateRepository @Inject constructor(
    private val apiService: APIService
) {
    val collabs = mutableStateListOf<UserContainer>()

    suspend fun searchBusiness(query: String) {
        val response = apiService.request<List<UserContainer>>(
            endpoint = "/search-business?q=$query",
            method = "GET",
            requireAuth = true
        )

        collabs.clear()
        collabs.addAll(response)
    }

    suspend fun postImage(
        imageBytes: ByteArray,
        caption: String,
        collab: String?
    ): PostModel {
        return apiService.multipartRequest(
            endpoint = "/posts/",
            method = "POST",
            imageBytes = imageBytes,
            imageKey = "image",
            params = mapOf(
                "caption" to caption,
                "collaboration" to (collab ?: "null")
            ),
            requireAuth = true
        )
    }
}