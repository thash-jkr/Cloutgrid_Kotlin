package com.cloutgrid.androidapp.ui.shared

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cloutgrid.androidapp.data.model.PostModel
import com.cloutgrid.androidapp.ui.screens.collab.CollabScreen
import com.cloutgrid.androidapp.ui.screens.create.CreateScreen
import com.cloutgrid.androidapp.ui.screens.home.HomeScreen
import com.cloutgrid.androidapp.ui.screens.profile.ProfileScreen
import com.cloutgrid.androidapp.ui.screens.search.SearchScreen
import com.cloutgrid.androidapp.ui.theme.First
import com.cloutgrid.androidapp.ui.theme.OffWhite
import com.cloutgrid.androidapp.ui.theme.Second
import kotlinx.coroutines.launch

enum class TabItem(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Rounded.Home),
    Search("Search", Icons.Rounded.Search),
    Create("Create", Icons.Rounded.AddCircle),
    Jobs("Jobs", Icons.Rounded.Work),
    Profile("Profile", Icons.Rounded.Person)
}

@Composable
fun TabNavigator(
    onNavigateToChatScreen: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToPostDetail: (PostModel, Boolean) -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
    onNavigateToCreatePost: (Uri) -> Unit,
    onNavigateToQuestions: (Int) -> Unit,
) {
    val tabs = TabItem.entries.toTypedArray()
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val selectTab: (TabItem) -> Unit = { tab ->
        coroutineScope.launch {
            pagerState.animateScrollToPage(tab.ordinal)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(vertical = 15.dp, horizontal = 15.dp)
                    .height(70.dp),
                shape = CircleShape,
                color = OffWhite,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val isSelected = pagerState.currentPage == index

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    coroutineScope.launch {
                                        pagerState.scrollToPage(index)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Second
                                            else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        modifier = Modifier.size(25.dp),
                                        tint = if (isSelected) Color.White else First
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxSize()
        ) { pageIndex ->
            when (tabs[pageIndex]) {
                TabItem.Home -> HomeScreen(
                    scaffoldPadding = paddingValues,
                    onSelectTab = selectTab,
                    onNavigateToChatScreen = onNavigateToChatScreen,
                    onNavigateToOtherProfile = onNavigateToOtherProfile,
                )
                TabItem.Search -> SearchScreen(
                    scaffoldPadding = paddingValues,
                    onSelectTab = selectTab,
                    onNavigateToOtherProfile = onNavigateToOtherProfile
                )
                TabItem.Create -> CreateScreen(
                    onNavigateToCreatePost = onNavigateToCreatePost
                )
                TabItem.Jobs -> CollabScreen(
                    scaffoldPadding = paddingValues,
                    onNavigateToQuestions = onNavigateToQuestions,
                    onNavigateToOtherProfile = onNavigateToOtherProfile
                )
                TabItem.Profile -> ProfileScreen(
                    scaffoldPadding = paddingValues,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToPostDetail = onNavigateToPostDetail
                )
            }
        }
    }
}
