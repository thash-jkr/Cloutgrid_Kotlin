package com.cloutgrid.androidapp.ui.screens.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloutgrid.androidapp.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchManager @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    val suggestions = searchRepository.suggestions
    val results = searchRepository.results
    val collabs = searchRepository.collabs

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchSuggestions() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                searchRepository.fetchSuggestions()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Failed to load suggestions"
            } finally {
                isLoading = false
            }
        }
    }

    fun handleSearch(query: String) {
        viewModelScope.launch {
            errorMessage = null

            try {
                searchRepository.handleSearch(query)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            }
        }
    }

    fun handleSearchBusiness(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                searchRepository.handleSearchBusiness(query)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }
}