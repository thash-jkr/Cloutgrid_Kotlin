package com.cloutgrid.androidapp.ui.screens.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.model.CommentModel
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.data.repository.ProfileRepository
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.repository.AuthRepository
import com.cloutgrid.androidapp.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileManager @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    val user = authRepository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val posts get() = profileRepository.posts
    val collabs get() = profileRepository.collabs

    var otherProfile by mutableStateOf<UserContainer?>(null)
        private set

    val otherPosts = mutableStateListOf<PostModel>()
    val otherCollabs = mutableStateListOf<PostModel>()

    val comments = mutableStateListOf<CommentModel>()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    suspend fun fetchProfile(username: String, other: Boolean) {
        isLoading = true
        errorMessage = null
        if (other) otherProfile = null

        try {
            val response = profileRepository.fetchProfile(username)
            if (other) {
                otherProfile = response
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage ?: "An error occurred"
        }
    }

    fun updateProfile(type: String, data: Map<String, String>, imageBytes: ByteArray?) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                authRepository.updateProfile(
                    type,
                    data,
                    imageBytes
                )
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Failed to update profile"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    suspend fun fetchPosts(username: String, other: Boolean = false) {
        isLoading = true
        errorMessage = null
        if (other) otherPosts.clear()

        try {
            val response = profileRepository.fetchPosts(username, other)
            if (other) {
                otherPosts.addAll(response)
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage ?: "An error occurred"
        }
    }

    fun likePost(postID: Int) {
        viewModelScope.launch {
            try {
                val response = homeRepository.likePost(postID)

                val postIndex = posts.indexOfFirst { it.id == postID }
                if (postIndex != -1) {
                    posts[postIndex] = posts[postIndex].copy(
                        likeCount = response.likeCount,
                        isLiked = response.liked
                    )
                }

                val collabIndex = collabs.indexOfFirst { it.id == postID }
                if (collabIndex != -1) {
                    collabs[collabIndex] = collabs[collabIndex].copy(
                        likeCount = response.likeCount,
                        isLiked = response.liked
                    )
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }

    fun fetchComments(postID: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            comments.clear()

            try {
                val results = homeRepository.fetchComments(postID)
                comments.addAll(results)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun addComment(postID: Int, content: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val newComment = homeRepository.addComment(postID, content)
                comments.add(0, newComment)

                val postIndex = posts.indexOfFirst { it.id == postID }
                if (postIndex != -1) {
                    posts[postIndex] = posts[postIndex].copy(commentCount = posts[postIndex].commentCount + 1)
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteComment(postID: Int, commentID: Int) {
        viewModelScope.launch {
            errorMessage = null
            try {
                homeRepository.deleteComment(postID, commentID)
                comments.removeAll { it.id == commentID }

                val postIndex = posts.indexOfFirst { it.id == postID }
                if (postIndex != -1) {
                    posts[postIndex] = posts[postIndex].copy(commentCount = posts[postIndex].commentCount - 1)
                }

                Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun fetchCollabs(username: String, other: Boolean = false) {
        isLoading = true
        errorMessage = null
        if (other) otherCollabs.clear()

        try {
            val response = profileRepository.fetchCollabs(username, other)
            if (other) {
                otherCollabs.addAll(response)
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            errorMessage = e.localizedMessage ?: "An error occurred"
        }
    }

    suspend fun handleBlock(username: String, block: Boolean) {
        isLoading = true
        errorMessage = null

        try {
            profileRepository.handleBlock(username, block)

            Toast.makeText(
                context,
                "You have ${if (block) "blocked" else "unblocked"} @$username",
                Toast.LENGTH_SHORT
            ).show()

            otherProfile = otherProfile?.copy(isBlocking = block)
            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "An error occurred"
            isLoading = false
        }
    }

    suspend fun handleFollow(username: String, follow: Boolean) {
        errorMessage = null

        try {
            profileRepository.handleFollow(username, follow)

            Toast.makeText(
                context,
                "You have ${if (follow) "followed" else "unfollowed"} @$username",
                Toast.LENGTH_SHORT
            ).show()

            otherProfile = otherProfile?.let { currentProfile ->
                val updatedCount = currentProfile.profile.followersCount + (if (follow) 1 else -1)
                currentProfile.copy(
                    isFollowing = follow,
                    profile = currentProfile.profile.copy(followersCount = updatedCount)
                )
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "An error occurred"
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}