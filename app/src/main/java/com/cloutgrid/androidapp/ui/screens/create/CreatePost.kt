package com.cloutgrid.androidapp.ui.screens.create

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.ui.components.CategoryList
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import com.cloutgrid.androidapp.ui.components.Empty
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreatePost(
    selectedImage: Uri,
    onNavigateBack: () -> Unit,
    create: CreateManager = hiltViewModel()
) {
    var caption by remember { mutableStateOf("") }
    var collab by remember { mutableStateOf<UserContainer?>(null) }
    val context = LocalContext.current

    var showCollabSheet by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val isImeVisible = WindowInsets.isImeVisible

    val user by create.user.collectAsState()
    var isCreator by remember(user) { mutableStateOf(user?.profile?.userType == "creator") }

    LaunchedEffect(Unit) {
        create.events.collect { success ->
            if (success) {
                Toast.makeText(context, "Posted", Toast.LENGTH_LONG).show()
                onNavigateBack()
            } else {
                Toast.makeText(context, create.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(isImeVisible) {
        if (isImeVisible && !showCollabSheet) {
            delay(150.milliseconds)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.ime),
        topBar = {
            CloutHeader(
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onNavigateBack
                ),
                actions = listOf(
                    HeaderAction(
                        icon = Icons.Outlined.Save,
                        contentDescription = "Save",
                        onClick = {
                            if (caption.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Caption cannot be empty",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val stream = ByteArrayOutputStream()
                                val bitmap = uriToBitmap(
                                    context,
                                    selectedImage
                                )

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)

                                create.handlePostImage(
                                    stream.toByteArray(),
                                    caption,
                                    collab?.profile?.username
                                )
                            }
                        }
                    ),
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .imePadding(),
        ) {
            AsyncImage(
                model = selectedImage,
                contentDescription = "Loaded image from URI",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp),
                maxLines = 5
            )

            if (isCreator) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showCollabSheet = true
                        }
                ) {
                    OutlinedTextField(
                        value = if (collab != null) "@${collab?.profile?.username}" else "",
                        onValueChange = {},
                        label = { Text("Collaboration") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (collab != null) {
                                IconButton(onClick = { collab = null }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear selected collaboration"
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        if (showCollabSheet) {
            CloutSheet(
                onDismiss = { showCollabSheet = false },
                content = {
                    CollabSheet(
                        create,
                        selectedCollab = collab,
                        onCollabSelected = {
                            collab = it
                            showCollabSheet = false
                        },
                    )
                }
            )
        }
    }
}

@Composable
private fun CollabSheet(
    create: CreateManager,
    selectedCollab: UserContainer?,
    onCollabSelected: (UserContainer) -> Unit
) {
    var query by remember { mutableStateOf("") }

    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            create.searchBusiness(query)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Search Collaboration",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    start = 10.dp,
                    end = 10.dp
                )
            ) {
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search for Businesses") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (create.collabs.isEmpty()) {
                    item {
                        Empty(
                            type = "general",
                            message = if (query.isEmpty()) "Search for the business you collaborated with"
                            else "No results found",
                            isLoading = create.isLoading
                        )
                    }
                }

                items(items = create.collabs) { collab ->
                    ListItem(
                        leadingContent = @Composable {
                            AsyncImage(
                                model = ApiConfig.current.baseURL + collab.profile.profilePhoto,
                                contentDescription = "${collab.profile.name}'s profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                        },
                        supportingContent = @Composable {
                            Text(CategoryList.label(collab.targetAudience ?: ""))
                        },
                        selected = collab.profile.username == selectedCollab?.profile?.username,
                        onClick = {
                            onCollabSelected(collab)

                            query = ""
                        }
                    ) {
                        Text(collab.profile.name)
                    }
                }
            }
        }
    }
}