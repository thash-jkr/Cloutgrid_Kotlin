package com.cloutgrid.androidapp.ui.screens.profile

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import com.cloutgrid.androidapp.ui.components.ReportBox
import com.cloutgrid.androidapp.ui.components.ListRow
import androidx.core.net.toUri
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
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
        modifier = Modifier.fillMaxSize(),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = OffWhite
                    )
                ) {
                    Column {
                        ListRow(
                            "Help",
                            {showHelpDialog = true},
                            Icons.AutoMirrored.Outlined.HelpOutline,
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                        ListRow(
                            title = "Privacy Policy",
                            icon = Icons.Outlined.PrivacyTip,
                            onClick = {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://cloutgrid.com/privacypolicy".toUri()
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Failed to open privacy policy",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            endIcon = Icons.AutoMirrored.Outlined.OpenInNew
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                        ListRow(
                            title = "EULA",
                            icon = Icons.Outlined.Gavel,
                            endIcon = Icons.AutoMirrored.Outlined.OpenInNew,
                            onClick = {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        "https://cloutgrid.com/eula".toUri()
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Failed to open EULA",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                        ListRow(
                            title = "Feedback",
                            icon = Icons.Outlined.Feedback,
                            onClick = { showFeedbackDialog = true }
                        )

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)

                        ListRow(
                            title = "Security",
                            icon = Icons.Outlined.Security,
                            onClick = { onNavigateToSecurity() }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = OffWhite
                    )
                ) {
                    ListRow(
                        title = "Logout",
                        icon = Icons.AutoMirrored.Outlined.Logout,
                        onClick = { showLogoutDialog = true },
                        danger = true
                    )
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