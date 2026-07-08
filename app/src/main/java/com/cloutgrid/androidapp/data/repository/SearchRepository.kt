package com.cloutgrid.androidapp.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.cloutgrid.androidapp.data.model.AllUsersResponse
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.APIService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val apiService: APIService
) {
    val suggestions = mutableStateListOf<UserContainer>()
    val results = mutableStateListOf<UserContainer>()
    val collabs = mutableStateListOf<UserContainer>()

    suspend fun fetchSuggestions() {
        val response = apiService.request<AllUsersResponse>(
            endpoint = "/users/",
            method = "GET",
            requireAuth = true
        )

        val allUsers = response.creators + response.businesses

        suggestions.clear()
        suggestions.addAll(allUsers.shuffled().take(6))
    }

    suspend fun handleSearch(query: String) {
        val response = apiService.request<AllUsersResponse>(
            endpoint = "/search?q=$query",
            method = "GET",
            requireAuth = true
        )

        val allUsers = response.creators + response.businesses

        results.clear()
        results.addAll(allUsers)
    }

    suspend fun handleSearchBusiness(query: String) {
        val response = apiService.request<List<UserContainer>>(
            endpoint = "/search-business?q=$query",
            method = "GET",
            requireAuth = true
        )

        collabs.clear()
        collabs.addAll(response)
    }
}