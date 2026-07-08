package com.cloutgrid.androidapp.ui.screens.create

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cloutgrid.androidapp.data.repository.AuthRepository
import com.cloutgrid.androidapp.data.repository.CreateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class CreateManager @Inject constructor(
    authRepository: AuthRepository,
    private val createRepository: CreateRepository,
    @ApplicationContext val context: Context
) : ViewModel() {
    val user = authRepository.user.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
}