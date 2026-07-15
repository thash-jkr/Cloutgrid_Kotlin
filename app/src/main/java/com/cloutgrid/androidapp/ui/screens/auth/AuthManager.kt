package com.cloutgrid.androidapp.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    lateinit var selectedType: String

    val isAuth: StateFlow<Boolean?> = authRepository.isAuth
    val user: StateFlow<UserContainer?> = authRepository.user

    var name = TextFieldState()
    var username = TextFieldState()
    var email = TextFieldState()
    var password = TextFieldState()
    var category by mutableStateOf("")
    var otp = TextFieldState()

    var type by mutableStateOf("")

    var emailSend by mutableStateOf(false)
    var emailVerified by mutableStateOf(false)

    private val _events = Channel<Boolean>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)

    fun login(email: String, password: String, type: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

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

    fun handleOTP(
        type: String,
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            if (name.text.toString() == "" || username.text.toString() == "" || email.text.toString() == "") {
                errorMessage = "Please fill in all fields"
                _events.send(false)
                return@launch
            }

            try {
                val data = if (type == "send") {
                    mapOf(
                        "name" to name.text.toString(),
                        "username" to username.text.toString(),
                        "email" to email.text.toString(),
                    )
                } else {
                    mapOf(
                        "username" to username.text.toString(),
                        "otp" to otp.text.toString()
                    )
                }

                authRepository.handleOTP(data, type)
                successMessage = if (type == "send") "OTP sent to email" else "Email verified"

                if (type == "send") {
                    emailSend = true
                } else {
                    emailVerified = true
                }

                _events.send(true)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred during authentication"
                _events.send(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun handleRegister(
        type: String,
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            if (
                name.text.toString() == "" ||
                username.text.toString() == "" ||
                email.text.toString() == "" ||
                password.text.toString() == "" ||
                category == ""
            ) {
                errorMessage = "Please fill in all fields"
                _events.send(false)
                return@launch
            }

            try {
                var data = mapOf(
                    "user.name" to name.text.toString(),
                    "user.username" to username.text.toString(),
                    "user.email" to email.text.toString(),
                    "user.password" to password.text.toString(),
                )

                data = if (type == "Creator") {
                    data.plus(mapOf("area" to category))
                } else {
                    data.plus(mapOf("target_audience" to type))
                }

                authRepository.register(data, type = if (type == "Creator") "creator" else "business")
                successMessage = "Registration successful, Please Login"
                _events.send(true)
            } catch (
                e: Exception
            ) {
                errorMessage = e.localizedMessage ?: "An error occurred during authentication"
                _events.send(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun handleResetPassword() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            try {
                authRepository.resetPassword(email.text.toString())
                successMessage = "Password reset email sent"
                _events.send(true)
            } catch (
                e: Exception
            ) {
                errorMessage = e.localizedMessage ?: "An error occurred during authentication"
                _events.send(false)
            } finally {
                isLoading = false
            }
        }
    }
}