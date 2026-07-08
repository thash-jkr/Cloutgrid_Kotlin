package com.cloutgrid.androidapp.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.APIService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateRepository @Inject constructor(
    private val apiService: APIService
) {
    val collabs = mutableStateListOf<UserContainer>()
}