package com.cloutgrid.androidapp.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.cloutgrid.androidapp.data.model.*
import com.cloutgrid.androidapp.data.network.APIService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: APIService
) {
    val notifications = mutableStateListOf<NotificationModel>()
    val posts = mutableStateListOf<PostModel>()

    var nextCursor by mutableStateOf<String?>(null)
        private set

    suspend fun fetchNotifications() {
        val results = apiService.request<List<NotificationModel>>(
            endpoint = "/notifications/?all=false/",
            method = "GET",
            requireAuth = true
        )
        notifications.clear()
        notifications.addAll(results)
    }

    suspend fun readNotification(id: Int) {
        apiService.request<EmptyResponse>(
            endpoint = "/notifications/$id/mark_as_read/",
            method = "POST",
            requireAuth = true
        )
        notifications.removeAll { it.id == id }
    }

    suspend fun fetchPosts(isFirstPage: Boolean) {
        val url = if (isFirstPage) "/posts/" else nextCursor

        if (url.isNullOrEmpty()) return

        val response = apiService.request<PostResponse>(
            endpoint = url,
            method = "GET",
            requireAuth = true,
            fullURL = !isFirstPage
        )

        if (isFirstPage) {
            posts.clear()
        }
        posts.addAll(response.results)
        nextCursor = response.next
    }

    fun addNewPost(post: PostModel) {
        posts.add(0, post)
    }

    suspend fun likePost(postID: Int): LikeResponse {
        return apiService.request<LikeResponse>(
            endpoint = "/posts/$postID/like/",
            method = "POST",
            body = emptyMap<String, String>(),
            requireAuth = true
        )
    }

    suspend fun deletePost(postID: Int) {
        apiService.request<EmptyResponse>(
            endpoint = "/posts/$postID/",
            method = "DELETE",
            requireAuth = true
        )

        posts.removeAll { it.id == postID }
    }

    suspend fun fetchComments(postID: Int): List<CommentModel> {
        return apiService.request(
            endpoint = "/posts/$postID/comments/",
            method = "GET",
            requireAuth = true
        )
    }

    suspend fun addComment(postID: Int, content: String): CommentModel {
        return apiService.request<CommentModel>(
            endpoint = "/posts/$postID/comments/",
            method = "POST",
            body = mapOf("content" to content),
            requireAuth = true
        )
    }

    suspend fun deleteComment(postID: Int, commentID: Int) {
        apiService.request<EmptyResponse>(
            endpoint = "/posts/$postID/comment/$commentID/",
            method = "DELETE",
            requireAuth = true
        )
    }

    fun handleBlock(username: String) {
        posts.removeAll { it.postedBy.profile.username == username }
    }
}