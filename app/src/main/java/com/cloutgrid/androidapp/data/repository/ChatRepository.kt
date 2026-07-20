package com.cloutgrid.androidapp.data.repository

import com.cloutgrid.androidapp.data.model.ConversationModel
import com.cloutgrid.androidapp.data.model.MessageModel
import com.cloutgrid.androidapp.data.network.APIService
import com.cloutgrid.androidapp.data.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val apiService: APIService,
    private val client: HttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val socketScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var webSocketSession: io.ktor.client.plugins.websocket.DefaultClientWebSocketSession? = null

    private val _incomingMessages = MutableSharedFlow<MessageModel>(extraBufferCapacity = 16)
    val incomingMessages: SharedFlow<MessageModel> = _incomingMessages.asSharedFlow()

    private val _socketConnected = MutableSharedFlow<Boolean>(replay = 1, extraBufferCapacity = 1)
    val socketConnected: SharedFlow<Boolean> = _socketConnected.asSharedFlow()

    suspend fun fetchConversations(): List<ConversationModel> {
        return apiService.request(
            endpoint = "/chats/",
            method = "GET",
            requireAuth = true
        )
    }

    suspend fun createConversation(id: Int): ConversationModel {
        return apiService.request(
            endpoint = "/chats/$id/",
            method = "POST",
            body = emptyMap<String, String>(),
            requireAuth = true
        )
    }

    suspend fun fetchMessages(conversationId: String): List<MessageModel> {
        return apiService.request(
            endpoint = "/chats/${conversationId.lowercase()}/messages/",
            method = "GET",
            requireAuth = true
        )
    }

    suspend fun connectWebSocket(conversationId: String, token: String) {
        if (webSocketSession != null) return

        try {
            val session = client.webSocketSession(
                urlString = "${ApiConfig.current.socketURL}/chat/${conversationId.lowercase()}/?token=$token"
            )
            webSocketSession = session
            _socketConnected.emit(true)

            listenForLiveMessages(session)
        } catch (e: Exception) {
            _socketConnected.emit(false)
        }
    }

    fun disconnectWebSocket() {
        socketScope.launch {
            webSocketSession?.close()
            webSocketSession = null
            _socketConnected.emit(false)
        }
    }

    fun sendLiveMessage(content: String) {
        val session = webSocketSession ?: return

        socketScope.launch {
            val payload = json.encodeToString(mapOf("content" to content))
            session.send(Frame.Text(payload))
        }
    }

    private fun listenForLiveMessages(session: io.ktor.client.plugins.websocket.DefaultClientWebSocketSession) {
        socketScope.launch {
            try {
                for (frame in session.incoming) {
                    if (frame is Frame.Text) {
                        parseAndEmitIncomingMessage(frame.readText())
                    }
                }
            } catch (e: Exception) {
                _socketConnected.emit(false)
                webSocketSession = null
            }
        }
    }

    private suspend fun parseAndEmitIncomingMessage(jsonString: String) {
        try {
            val message = json.decodeFromString<MessageModel>(jsonString)
            _incomingMessages.emit(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}