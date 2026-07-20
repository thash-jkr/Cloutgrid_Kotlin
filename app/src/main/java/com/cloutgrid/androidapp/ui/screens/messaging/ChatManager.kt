package com.cloutgrid.androidapp.ui.screens.messaging

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.model.ConversationModel
import com.cloutgrid.androidapp.data.model.MessageModel
import com.cloutgrid.androidapp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatManager @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    val chats = mutableStateListOf<ConversationModel>()
    val messages = mutableStateListOf<MessageModel>()

    var isLoading by mutableStateOf(false)
        private set
    var socketConnected by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var newConversationId by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            chatRepository.socketConnected.collect { connected ->
                socketConnected = connected
            }
        }

        viewModelScope.launch {
            chatRepository.incomingMessages.collect { incoming ->
                if (messages.none { it.id == incoming.id }) {
                    messages.add(incoming)
                }
            }
        }
    }

    fun fetchConversations() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = chatRepository.fetchConversations()
                chats.clear()
                chats.addAll(response)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun createConversation(id: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = chatRepository.createConversation(id)
                chats.add(response)
                newConversationId = response.id
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchMessages(id: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = chatRepository.fetchMessages(id)
                messages.clear()
                messages.addAll(response)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "An error occurred"
            } finally {
                isLoading = false
            }
        }
    }

    fun connectWebSocket(conversationId: String, token: String) {
        viewModelScope.launch {
            chatRepository.connectWebSocket(conversationId, token)
        }
    }

    fun disconnectWebSocket() {
        chatRepository.disconnectWebSocket()
    }

    fun sendLiveMessage(content: String) {
        chatRepository.sendLiveMessage(content)
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}