    package com.cloutgrid.androidapp.ui.screens.profile

    import android.graphics.Bitmap
    import android.net.Uri
    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.Button
    import androidx.compose.material3.Text
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.viewinterop.AndroidView
    import com.canhub.cropper.CropImageView

    @Composable
    fun ProfileImageCropper(
        imageUri: Uri,
        onCropCompleted: (Bitmap?) -> Unit,
        onCancelled: () -> Unit
    ) {
        var cropperInstance: CropImageView? by remember { mutableStateOf(null) }

        Column(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { ctx ->
                    CropImageView(ctx).apply {
                        setAspectRatio(1, 1)
                        setFixedAspectRatio(true)
                        cropShape = CropImageView.CropShape.OVAL
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
                }
            )

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