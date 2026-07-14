package com.cloutgrid.androidapp.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.ui.screens.auth.AuthManager
import com.cloutgrid.androidapp.ui.shared.AuthNavigation

@Composable
fun AppCoordinator(
    authManager: AuthManager = hiltViewModel()
) {
    val isAuth by authManager.isAuth.collectAsState()

    if (isAuth) {
        AppNavigation()
    } else {
        AuthNavigation()
    }
}