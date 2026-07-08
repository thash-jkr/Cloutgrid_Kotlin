package com.cloutgrid.androidapp.ui.screens.create

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.canhub.cropper.CropImageView

@Composable
fun SquareCrop(
    imageUri: Uri,
    onCropCompleted: (Bitmap?) -> Unit,
    onCancelled: () -> Unit
) {
    var cropperInstance: CropImageView? by remember { mutableStateOf(null) }
    var ratioX by remember { mutableIntStateOf(1) }
    var ratioY by remember { mutableIntStateOf(1) }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { ctx ->
                CropImageView(ctx).apply {
                    setFixedAspectRatio(true)
                    cropShape = CropImageView.CropShape.RECTANGLE
                    guidelines = CropImageView.Guidelines.ON

                    setOnCropImageCompleteListener { _, result ->
                        onCropCompleted(result.bitmap)
                    }

                    setImageUriAsync(imageUri)
                    cropperInstance = this
                }
            },
            update = { view ->
                cropperInstance = view
                view.setAspectRatio(ratioX, ratioY)
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToggleButton(
                checked = ratioX == 1 && ratioY == 1,
                onCheckedChange = {
                    ratioX = 1
                    ratioY = 1
                },
            ) {
                Text("1:1")
            }

            ToggleButton(
                checked = ratioX == 4 && ratioY == 3,
                onCheckedChange = {
                    ratioX = 4
                    ratioY = 3
                },
            ) {
                Text("4:3")
            }

            ToggleButton(
                checked = ratioX == 3 && ratioY == 4,
                onCheckedChange = {
                    ratioX = 3
                    ratioY = 4
                },
            ) {
                Text("3:4")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onCancelled) {
                Text("Cancel")
            }

            Button(onClick = {
                cropperInstance?.croppedImageAsync()
            }) {
                Text("Crop & Save")
            }
        }
    }
}