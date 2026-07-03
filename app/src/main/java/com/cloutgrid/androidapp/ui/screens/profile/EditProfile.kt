package com.cloutgrid.androidapp.ui.screens.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.data.network.ApiConfig
import com.cloutgrid.androidapp.models.CategoryList
import com.cloutgrid.androidapp.ui.components.CategorySheet
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import io.ktor.util.collections.getValue
import io.ktor.utils.io.InternalAPI
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class, InternalAPI::class)
@Composable
fun EditProfile(
    onNavigateBack: () -> Unit,
    profile: ProfileManager = hiltViewModel()
) {
    var rawImageURI by remember { mutableStateOf<Uri?>(null) }
    var showEditImage by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    var selectedBytes by remember { mutableStateOf<ByteArray?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            rawImageURI = uri
            showEditImage = true
        }
    }

    val user by profile.user.collectAsState()
    val type = user?.profile?.userType ?: "creator"

    val isLoaded = user != null

    val nameState = remember(isLoaded) {
        TextFieldState(user?.profile?.name ?: "")
    }
    val bioState = remember(isLoaded) {
        TextFieldState(user?.profile?.bio ?: "")
    }
    val websiteState = remember(isLoaded) {
        TextFieldState(user?.website ?: "")
    }
    var category by remember(isLoaded) { mutableStateOf(user?.area ?: user?.targetAudience ?: "") }

    val scrollState = rememberScrollState()
    var showCategorySheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Edit Profile",
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
                            var data = mapOf(
                                "user[name]" to nameState.text.toString().trim(),
                                "user[bio]" to bioState.text.toString().trim(),
                            )

                            data = if (type == "creator") {
                                data + mapOf(
                                    "area" to category
                                )
                            } else {
                                data + mapOf(
                                    "website" to websiteState.text.toString().trim(),
                                    "target_audience" to category
                                )
                            }

                            profile.updateProfile(
                                type,
                                data,
                                if (selectedImage != null) selectedBytes else null
                            )

                            if (profile.errorMessage == null) {
                                Toast.makeText(
                                    profile.context,
                                    "Profile Updated",
                                    Toast.LENGTH_LONG
                                ).show()
                                onNavigateBack()
                            }
                        }
                    )
                )
            )
        }
    ) { innerPadding ->
        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (selectedImage == null) {
                    AsyncImage(
                        model = ApiConfig.current.baseURL + user?.profile?.profilePhoto,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        bitmap = selectedImage!!.asImageBitmap(),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .offset(y = (-30).dp)
                        .size(25.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                state = nameState,
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            )

            OutlinedTextField(
                state = bioState,
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            )

            if (type == "business") {
                OutlinedTextField(
                    state = websiteState,
                    label = { Text("Website") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showCategorySheet = true
                    }
            ) {
                OutlinedTextField(
                    value = CategoryList.label(category),
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        if (showCategorySheet) {
            CloutSheet(
                onDismiss = {
                    showCategorySheet = false
                },
                content = {
                    CategorySheet(CategoryList.allOptions, category) { cat ->
                        category = cat.value
                        showCategorySheet = false
                    }
                }
            )
        }

        if (showEditImage && rawImageURI != null) {
            Dialog(
                onDismissRequest = { showEditImage = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                ProfileImageCropper(
                    imageUri = rawImageURI!!,
                    onCropCompleted = { bitmap ->
                        showEditImage = false
                        if (bitmap != null) {
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                            selectedImage = bitmap
                            selectedBytes = stream.toByteArray()
                        }
                    },
                    onCancelled = { showEditImage = false }
                )
            }
        }
    }
}