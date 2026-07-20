package com.cloutgrid.androidapp.ui.screens.profile

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.ReportBox
import com.cloutgrid.androidapp.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    onNavigateBack: () -> Unit,
    profile: ProfileManager = hiltViewModel(),
    onNavigateToSecurity: () -> Unit
) {
    var showHelpDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CloutHeader(
                title = "Settings",
                icon= HeaderAction(
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    onClick = onNavigateBack
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 0, count = 4),
                    leadingContent = {
                        Icon(
                            Icons.AutoMirrored.Rounded.Help,
                            contentDescription = "Help",
                        )
                    },
                    onClick = {
                        showHelpDialog = true
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                ) {
                    Text("Help")
                }

                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 1, count = 4),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.PrivacyTip,
                            contentDescription = "Privacy Icon",
                        )
                    },
                    onClick = {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://cloutgrid.com/privacypolicy".toUri()
                            )
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(
                                context,
                                "Failed to open privacy policy",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = "Open in new tab",
                        )
                    }
                ) {
                    Text("Privacy Policy")
                }

                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 2, count = 4),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Gavel,
                            contentDescription = "EULA",
                        )
                    },
                    onClick = {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://cloutgrid.com/eula".toUri()
                            )
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(
                                context,
                                "Failed to open EULA",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = "Open in new tab",
                        )
                    }
                ) {
                    Text("EULA")
                }

                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 3, count = 4),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Feedback,
                            contentDescription = "Feedback",
                        )
                    },
                    onClick = {
                        showFeedbackDialog = true
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                ) {
                    Text("Feedback")
                }
            }

            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateBottomPadding(),
                        start = 16.dp,
                        end = 16.dp,
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Security,
                            contentDescription = "Security",
                        )
                    },
                    onClick = {
                        onNavigateToSecurity()
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                ) {
                    Text("Security")
                }

                SegmentedListItem(
                    shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                    leadingContent = {
                        Icon(
                            Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = "Logout",
                        )
                    },
                    onClick = {
                        showLogoutDialog = true
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                ) {
                    Text("Logout")
                }
            }
        }

        if (showHelpDialog) {
            ReportBox(
                title = "Need Help?",
                body = "If you are facing any issue, let us know and our team will reach out to you soon",
                onDismiss = { showHelpDialog = false },
                onSubmit = {
                    showHelpDialog = false
                }
            )
        }

        if (showFeedbackDialog) {
            ReportBox(
                title = "Feedback",
                body = "If you have any suggestions or feedback, let us know and our team will improve our services",
                onDismiss = { showFeedbackDialog = false },
                onSubmit = {
                    showFeedbackDialog = false
                }
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            profile.logout()
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}