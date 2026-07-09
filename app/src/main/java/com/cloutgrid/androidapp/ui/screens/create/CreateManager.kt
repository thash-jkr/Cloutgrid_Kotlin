package com.cloutgrid.androidapp.ui.screens.create

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cloutgrid.androidapp.data.repository.AuthRepository
import com.cloutgrid.androidapp.data.repository.CreateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.repository.HomeRepository
import com.cloutgrid.androidapp.data.repository.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CreateManager @Inject constructor(
    authRepository: AuthRepository,
    private val homeRepository: HomeRepository,
    private val createRepository: CreateRepository,
    private val profileRepository: ProfileRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    val user = authRepository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _events = Channel<Boolean>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val collabs get() = createRepository.collabs

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun searchBusiness(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                createRepository.searchBusiness(query)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun handlePostImage(
        imageBytes: ByteArray,
        caption: String,
        collab: String?
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val newPost = createRepository.postImage(imageBytes, caption, collab)
                homeRepository.posts.add(0, newPost)
                profileRepository.posts.add(0, newPost)
                _events.send(true)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
                _events.send(false)
            } finally {
                isLoading = false
            }
        }
    }
}