package com.cloutgrid.androidapp.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.components.CloutCapsule
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.Empty
import com.cloutgrid.androidapp.ui.shared.TabItem
import com.cloutgrid.androidapp.ui.theme.OffWhite
import com.cloutgrid.androidapp.ui.theme.Second

@Composable
fun SearchScreen(
    scaffoldPadding: PaddingValues,
    onSelectTab: (TabItem) -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
    search: SearchManager = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }

    val userList = if (query.isEmpty()) search.suggestions else search.results

    LaunchedEffect(Unit) {
        if (search.suggestions.isEmpty()) {
            search.fetchSuggestions()
        }
    }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            search.handleSearch(query)
        }
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CloutHeader(
                title = "Connect"
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                search.fetchSuggestions()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            if (search.suggestions.isEmpty() && search.isLoading) {
                Column(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                ) {
                    Empty(
                        type = "general",
                        message = "Loading...",
                        isLoading = search.isLoading
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = scaffoldPadding.calculateBottomPadding(),
                        start = 10.dp,
                        end = 10.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Search") },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = OffWhite,
                                unfocusedContainerColor = OffWhite
                            )
                        )
                    }

                    if (userList.isEmpty() && query.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Empty(
                                type = "general",
                                message = "No results found",
                                isLoading = search.isLoading
                            )
                        }
                    } else {
                        items(userList, key = { it.profile.id }) { user ->
                            ElevatedCard(
                                onClick = {
                                    if (user.profile.username == "") {
                                        onSelectTab(TabItem.Profile)
                                    } else {
                                        onNavigateToOtherProfile(user.profile.username)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.elevatedCardElevation()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                                ) {
                                    AsyncImage(
                                        model = ApiConfig.current.baseURL + user.profile.profilePhoto,
                                        contentDescription = "${user.profile.name}'s profile photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                    )

                                    Text(
                                        text = user.profile.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )

                                    CloutCapsule(user.area ?: user.targetAudience ?: "Creator")

                                    Spacer(modifier = Modifier.height(5.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
