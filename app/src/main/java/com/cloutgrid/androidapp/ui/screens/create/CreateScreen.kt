package com.cloutgrid.androidapp.ui.screens.create

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.R
import com.cloutgrid.androidapp.ui.components.CloutHeader
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateScreen(
    create: CreateManager = hiltViewModel(),
    onNavigateToCreatePost: (Uri) -> Unit
) {
    val user by create.user.collectAsState()

    var isCreator by remember(user) { mutableStateOf(user?.profile?.userType == "creator") }

    var rawImageURI by remember { mutableStateOf<Uri?>(null) }
    var showEditImage by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            rawImageURI = uri
            showEditImage = true
        }
    }

    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Create",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp, 10.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = false
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f)
                    ) {
                        Text(
                            "Post",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text("Upload an image from a previous brand collaboration or campaign you've been part of")
                    }

                    Image(
                        painter = painterResource(id = R.drawable.create),
                        contentDescription = "Create Post",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(150.dp)
                    )
                }

                if (!isCreator) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp, 10.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                clip = false
                            )
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {

                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.collab_2),
                            contentDescription = "Create Collaboration",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .padding(10.dp)
                                .width(150.dp)
                        )

                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "Collaboration",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Post a collaboration opportunity to connect with creators who match your brand",
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }

        if (showEditImage && rawImageURI != null) {
            Dialog(
                onDismissRequest = { showEditImage = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                ),
            ) {
                SquareCrop(
                    imageUri = rawImageURI!!,
                    onCropCompleted = { bitmap ->
                        showEditImage = false
                        if (bitmap != null) {
                            selectedImage = bitmapToUri(
                                create.context,
                                bitmap
                            )
                            onNavigateToCreatePost(selectedImage!!)
                        }
                    },
                    onCancelled = { showEditImage = false }
                )
            }
        }
    }
}