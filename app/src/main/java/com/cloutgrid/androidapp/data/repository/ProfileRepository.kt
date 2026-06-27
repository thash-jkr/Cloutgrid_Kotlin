package com.cloutgrid.androidapp.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.cloutgrid.androidapp.data.model.EmptyResponse
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: APIService
) {
    val posts = mutableStateListOf<PostModel>()
    val collabs = mutableStateListOf<PostModel>()

    suspend fun fetchProfile(username: String): UserContainer = withContext(Dispatchers.IO) {
        apiService.request<UserContainer>(
            endpoint = "/profiles/$username/",
            method = "GET",
            requireAuth = true
        )
    }

    suspend fun fetchPosts(username: String, other: Boolean): List<PostModel> = withContext(Dispatchers.IO) {
        val response = apiService.request<List<PostModel>>(
            endpoint = "/posts/$username/",
            method = "GET",
            requireAuth = true,
            fullURL = false
        )

        if (!other) {
            withContext(Dispatchers.Main) {
                posts.clear()
                posts.addAll(response)
            }
        }
        response
    }

    suspend fun fetchCollabs(username: String, other: Boolean): List<PostModel> = withContext(Dispatchers.IO) {
        val endpoint = if (other) "/posts/collabs/$username/" else "/posts/collabs/"
        val response = apiService.request<List<PostModel>>(
            endpoint = endpoint,
            method = "GET",
            requireAuth = true,
            fullURL = false
        )

        if (!other) {
            withContext(Dispatchers.Main) {
                collabs.clear()
                collabs.addAll(response)
            }
        }
        response
    }

    suspend fun handleBlock(username: String, block: Boolean): EmptyResponse = withContext(Dispatchers.IO) {
        val action = if (block) "block" else "unblock"
        apiService.request<EmptyResponse>(
            endpoint = "/profiles/$username/$action/",
            method = "POST",
            requireAuth = true
        )
    }

    suspend fun handleFollow(username: String, follow: Boolean): EmptyResponse = withContext(Dispatchers.IO) {
        val action = if (follow) "follow" else "unfollow"
        apiService.request<EmptyResponse>(
            endpoint = "/profiles/$username/$action/",
            method = "POST",
            requireAuth = true
        )
    }
}