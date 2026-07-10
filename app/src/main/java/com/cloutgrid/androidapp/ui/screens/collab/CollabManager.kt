package com.cloutgrid.androidapp.ui.screens.collab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.model.ApplicationModel
import com.cloutgrid.androidapp.data.model.JobModel
import com.cloutgrid.androidapp.data.repository.AuthRepository
import com.cloutgrid.androidapp.data.repository.CollabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollabManager @Inject constructor(
    authRepository: AuthRepository,
    private val collabRepository: CollabRepository
): ViewModel() {
    val user = authRepository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val jobs = collabRepository.jobs
    val applications = collabRepository.applications

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var successMessage by mutableStateOf<String?>(null)
        private set

    private val _events = Channel<Boolean>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun fetchJobs() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                collabRepository.fetchJobs()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchBusinessJobs() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                collabRepository.fetchBusinessJobs()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchApplications(job: JobModel) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                collabRepository.fetchApplications(job.id)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun createJob(
        title: String,
        description: String,
        requirements: String,
        targetCreator: String,
        questions: List<String>
    ) {
        if (title.isEmpty() || description.isEmpty() || requirements.isEmpty() || targetCreator.isEmpty()) {
            errorMessage = "Please fill in all fields"
            viewModelScope.launch { _events.send(false) }
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                collabRepository.createJob(title, description, requirements, targetCreator, questions)
                _events.send(true)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
                _events.send(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteJob(id: Int) {
        viewModelScope.launch {
            errorMessage = null

            try {
                collabRepository.deleteJob(id)
                jobs.removeAll { it.id == id }
                applications.clear()
                _events.send(true)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
                _events.send(false)
            }
        }
    }

    fun submitApplication(id: Int, answers: Map<Int, String>) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                collabRepository.submitApplication(id, answers)

                val index = jobs.indexOfFirst { it.id == id }
                if (index != -1) {
                    jobs[index] = jobs[index].copy(isApplied = true)
                }

                successMessage = "Application submitted"
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