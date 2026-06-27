package com.cloutgrid.androidapp.ui.screens.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.data.repository.ProfileRepository
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileManager @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
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

            // 🌟 Built-in Toast for Success
            Toast.makeText(
                context,
                "You have ${if (block) "blocked" else "unblocked"} @$username",
                Toast.LENGTH_SHORT
            ).show()

            // Update UI state locally inside the Manager
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
}