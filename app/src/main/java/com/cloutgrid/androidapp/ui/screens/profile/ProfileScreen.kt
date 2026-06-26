package com.cloutgrid.androidapp.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.ui.screens.auth.AuthManager

@Composable
fun ProfileScreen(authManager: AuthManager = hiltViewModel()) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Button(onClick = {authManager.logout()}) {
            Text("Sign Out")
        }
    }
}