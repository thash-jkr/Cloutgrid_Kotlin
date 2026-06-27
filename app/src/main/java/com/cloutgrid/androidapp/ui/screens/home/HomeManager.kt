package com.cloutgrid.androidapp.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cloutgrid.androidapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.model.CommentModel
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

@HiltViewModel
class HomeManager @Inject constructor(
    authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    val user = authRepository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val posts = homeRepository.posts
    val notifications = homeRepository.notifications
    val comments = mutableStateListOf<CommentModel>()
    val nextCursor get() = homeRepository.nextCursor

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchNotifications() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                homeRepository.fetchNotifications()
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun readNotification(id: Int) {
        viewModelScope.launch {
            errorMessage = null
            try {
                homeRepository.readNotification(id)
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun fetchPosts(isFirstPage: Boolean = true) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            try {
                homeRepository.fetchPosts(isFirstPage)
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addNewPost(post: PostModel) {
        homeRepository.addNewPost(post)
    }

    fun likePost(postID: Int) {
        viewModelScope.launch {
            try {
                homeRepository.likePost(postID)
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }

    fun deletePost(postID: Int) {
        viewModelScope.launch {
            errorMessage = null
            try {
                homeRepository.deletePost(postID)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
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
                Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}