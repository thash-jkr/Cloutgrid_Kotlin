package com.cloutgrid.androidapp.ui.shared

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.ui.screens.create.CreatePost
import com.cloutgrid.androidapp.ui.screens.messaging.ChatScreen
import com.cloutgrid.androidapp.ui.screens.profile.EditProfile
import com.cloutgrid.androidapp.ui.screens.profile.OtherProfile
import com.cloutgrid.androidapp.ui.screens.profile.Settings
import com.cloutgrid.androidapp.ui.screens.profile.Security
import com.cloutgrid.androidapp.ui.screens.profile.PostDetail
import kotlinx.serialization.Serializable
import androidx.core.net.toUri

@Serializable object TabNavigator
@Serializable object ChatScreen
@Serializable object Settings
@Serializable object Security
@Serializable object EditProfile

@Serializable data class PostDetailRoute(
    val id: Int,
    val other: Boolean
)
@Serializable data class OtherProfileRoute(
    val username: String
)
@Serializable data class CreatePostRoute(
    val selectedImage: String
)

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
                },
                onNavigateToSettings = {
                    navController.navigate(Settings)
                },
                onNavigateToEditProfile = {
                    navController.navigate(EditProfile)
                },
                onNavigateToPostDetail = { post: PostModel, other: Boolean ->
                    navController.navigate(
                        PostDetailRoute(
                            post.id,
                            other = other
                        )
                    )
                },
                onNavigateToOtherProfile = { username: String ->
                    navController.navigate(
                        OtherProfileRoute(username)
                    )
                },
                onNavigateToCreatePost = { selectedImage: Uri ->
                    navController.navigate(
                        CreatePostRoute(
                            selectedImage.toString()
                        )
                    )
                }
            )
        }

        composable<ChatScreen> {
            ChatScreen()
        }

        composable<Settings> {
            Settings(
                {navController.popBackStack()},
                onNavigateToSecurity = {
                    navController.navigate(Security)
                }
            )
        }

        composable<Security> {
            Security(
                {navController.popBackStack()}
            )
        }

        composable<EditProfile> {
            EditProfile(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<PostDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<PostDetailRoute>()

            PostDetail(
                id = args.id,
                other = args.other,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<OtherProfileRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<OtherProfileRoute>()

            OtherProfile(
                username = args.username,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPostDetail = { post: PostModel, other: Boolean ->
                    navController.navigate(
                        PostDetailRoute(
                            post.id,
                            other = other
                        )
                    )
                }
            )
        }

        composable<CreatePostRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<CreatePostRoute>()

            CreatePost(
                selectedImage = args.selectedImage.toUri(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}