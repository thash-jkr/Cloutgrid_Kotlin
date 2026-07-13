package com.cloutgrid.androidapp.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthManager @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    val isAuth: StateFlow<Boolean> = authRepository.isAuth.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val user = authRepository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _events = Channel<Boolean>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String, type: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                authRepository.login(email, password, type)
                _events.send(true)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred during authentication"
                _events.send(false)
            } finally {
                isLoading = false
            }
        }
    }
}