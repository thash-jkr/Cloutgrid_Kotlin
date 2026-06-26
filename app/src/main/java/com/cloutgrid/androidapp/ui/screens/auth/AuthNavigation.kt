package com.cloutgrid.androidapp.ui.screens.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cloutgrid.androidapp.ui.screens.auth.register.BasicInfoScreen
import com.cloutgrid.androidapp.ui.screens.auth.register.MoreInfoScreen
import kotlinx.serialization.Serializable

@Serializable object LandingRoute
@Serializable object LoginRoute
@Serializable object ResetPasswordRoute
@Serializable data class BasicInfoRoute(val type: String)
@Serializable data class MoreInfoRoute(val type: String)

@Composable
fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LandingRoute
    ) {
        composable<LandingRoute> {
            LandingScreen(
                onNavigateToBasicInfo = { selectedType: String ->
                    navController.navigate(BasicInfoRoute(type = selectedType))
                },
                onNavigateToLogin = {
                    navController.navigate(LoginRoute)
                }
            )
        }

        composable<LoginRoute> {
            LoginScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<BasicInfoRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<BasicInfoRoute>()

            BasicInfoScreen(
                args.type,
                 onNavigateToMoreInfo = { selectedType: String ->
                    navController.navigate(MoreInfoRoute(type = selectedType))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<MoreInfoRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<MoreInfoRoute>()

            MoreInfoScreen(
                args.type,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}