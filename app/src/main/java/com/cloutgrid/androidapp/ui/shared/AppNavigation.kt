package com.cloutgrid.androidapp.ui.shared

import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.cloutgrid.androidapp.ui.screens.messaging.ChatScreen
import kotlinx.serialization.Serializable

@Serializable object TabNavigator
@Serializable object ChatScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TabNavigator
    ) {
        composable<TabNavigator> {
            TabNavigator(
                onNavigateToChatScreen = {
                    navController.navigate(ChatScreen)
                }
            )
        }

        composable<ChatScreen> {
            ChatScreen()
        }
    }
}