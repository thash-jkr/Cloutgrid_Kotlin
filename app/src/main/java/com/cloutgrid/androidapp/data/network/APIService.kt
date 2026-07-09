package com.cloutgrid.androidapp.data.network

import android.util.Log
import com.cloutgrid.androidapp.data.repository.AuthRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class APIService @Inject constructor(
    private val authRepository: AuthRepository
) {
    val baseURL = ApiConfig.current.baseURL

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }

        // AUTOMATIC REACTIVE TOKEN REFRESH ENGINE
        install(Auth) {
            bearer {
                loadTokens {
                    // Asynchronously pull current snapshots from DataStore
                    val access = authRepository.access.first()
                    val refresh = authRepository.refresh.first()

                    if (!access.isNullOrEmpty() && !refresh.isNullOrEmpty()) {
                        BearerTokens(access, refresh)
                    } else null
                }

                refreshTokens {
                    val refreshToken = authRepository.refresh.first() ?: return@refreshTokens null
                    try {
                        // Request a fresh pair of tokens from your Django backend
                        val response = client.post("$baseURL/token/refresh/") {
                            contentType(ContentType.Application.Json)
                            setBody(mapOf("refresh" to refreshToken))
                            markAsRefreshTokenRequest() // Prevents infinite loops
                        }

                        if (response.status.isSuccess()) {
                            val body = response.body<Map<String, String>>()
                            val newAccess = body["access"] ?: ""
                            val newRefresh = body["refresh"] ?: refreshToken

                            authRepository.updateTokens(newAccess, newRefresh)

                            BearerTokens(newAccess, newRefresh)
                        } else {
                            authRepository.clearSession()
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }

    /**
     * Standard Network Generic Request Method
     */
    suspend inline fun <reified T> request(
        endpoint: String,
        method: String,
        body: Any? = null,
        requireAuth: Boolean,
        fullURL: Boolean = false,
    ): T {
        val urlString = if (fullURL) endpoint else "$baseURL$endpoint"

        val response: HttpResponse = try {
            client.request(urlString) {
                this.method = HttpMethod.parse(method)

                if (!requireAuth) {
                    attributes.put(AuthCircuitBreaker, Unit)
                }

                if (body != null) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            }
        } catch (e: Exception) {
            Log.d("Network Error", e.message ?: "Unknown error")
            throw APIError.ServerError("Network connection failed")
        }

        if (response.status == HttpStatusCode.NoContent) {
            return com.cloutgrid.androidapp.data.model.EmptyResponse() as T
        }

        if (!response.status.isSuccess()) {
            val errorText = response.bodyAsText()
            val serverMessage = try {
                Json.parseToJsonElement(errorText).jsonObject["message"]?.jsonPrimitive?.content
            } catch (e: Exception) {
                null
            } ?: "An error occurred status code: ${response.status.value}"

            throw APIError.ServerError(serverMessage)
        }

        return try {
            response.body<T>()
        } catch (e: Exception) {
            throw APIError.DecodingError("Failed to decode response mapping.")
        }
    }

    @OptIn(InternalAPI::class)
    suspend inline fun <reified T> multipartRequest(
        endpoint: String,
        method: String,
        imageBytes: ByteArray?,
        imageKey: String?,
        params: Map<String, String>,
        requireAuth: Boolean
    ): T {
        val response: HttpResponse = try {
            client.submitFormWithBinaryData(
                url = "$baseURL$endpoint",
                formData = formData {
                    params.forEach { (key, value) ->
                        append(key, value)
                    }
                    if (imageBytes != null && imageKey != null) {
                        append(imageKey, imageBytes, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                        })
                    }
                }
            ) {
                this.method = HttpMethod.parse(method)

                if (!requireAuth) attributes.put(AuthCircuitBreaker, Unit)
            }
        } catch (e: Exception) {
            throw APIError.ServerError(e.message ?: "Network upload failed")
        }

        if (!response.status.isSuccess()) {
            val errorText = response.bodyAsText()
            val serverMessage = try {
                Json.parseToJsonElement(errorText).jsonObject["message"]?.jsonPrimitive?.content
            } catch (e: Exception) {
                null
            } ?: "An error occurred status code: ${response.status.value}"

            throw APIError.ServerError(serverMessage)
        }

        return try {
            response.body<T>()
        } catch (e: Exception) {
            throw APIError.DecodingError("Failed to decode response mapping.")
        }
    }
}