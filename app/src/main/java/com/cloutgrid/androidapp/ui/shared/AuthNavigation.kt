package com.cloutgrid.androidapp.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cloutgrid.androidapp.ui.screens.auth.AuthManager
import com.cloutgrid.androidapp.ui.screens.auth.LandingScreen
import com.cloutgrid.androidapp.ui.screens.auth.LoginScreen
import com.cloutgrid.androidapp.ui.screens.auth.ResetPassword
import com.cloutgrid.androidapp.ui.screens.auth.register.BasicInfoScreen
import com.cloutgrid.androidapp.ui.screens.auth.register.MoreInfoScreen
import kotlinx.serialization.Serializable

@Serializable object LandingRoute
@Serializable object LoginRoute
@Serializable object ResetPasswordRoute
@Serializable object RegisterFlow
@Serializable data class BasicInfoRoute(val type: String)
@Serializable object MoreInfoRoute

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
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResetPassword = {
                    navController.navigate(ResetPasswordRoute)
                }
            )
        }

        navigation<RegisterFlow>(startDestination = BasicInfoRoute::class) {
            composable<BasicInfoRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<BasicInfoRoute>()
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<RegisterFlow>()
                }
                val auth: AuthManager = hiltViewModel(parentEntry)

                BasicInfoScreen(
                    type = args.type,
                    auth = auth,
                    onNavigateToMoreInfo = {
                        navController.navigate(MoreInfoRoute)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<MoreInfoRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<RegisterFlow>()
                }
                val auth: AuthManager = hiltViewModel(parentEntry)

                MoreInfoScreen(
                    auth = auth,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(LoginRoute)
                    }
                )
            }
        }

        composable<ResetPasswordRoute> {
            ResetPassword(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}