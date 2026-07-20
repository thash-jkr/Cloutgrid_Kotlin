package com.cloutgrid.androidapp.ui.screens.auth.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.screens.auth.AuthManager

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BasicInfoScreen(
    type: String,
    onNavigateToMoreInfo: () -> Unit,
    onNavigateBack: () -> Unit,
    auth: AuthManager = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        auth.events.collect { success ->
            if (success) {
                Toast.makeText(context, auth.successMessage, Toast.LENGTH_LONG).show()
                if (auth.emailVerified) {
                    onNavigateToMoreInfo()
                }
            } else {
                Toast.makeText(context, auth.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(type) {
        auth.selectedType = type
    }

    Scaffold(
        topBar = {
            CloutHeader(
                title = "$type Registration",
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = { onNavigateBack() }
                )
            )
        }
    ) {
        innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    state = auth.name,
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    state = auth.username,
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = !auth.emailVerified,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedTextField(
                    state = auth.email,
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = !auth.emailVerified,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    )
                )

                if (auth.emailSend && !auth.emailVerified) {
                    OutlinedTextField(
                        state = auth.otp,
                        label = { Text("OTP") },
                        placeholder = { Text("Enter the OTP sent to your email") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (auth.isLoading) {
                        LoadingIndicator()
                    } else {
                        Button(
                            {
                                if (auth.emailVerified) {
                                    onNavigateToMoreInfo()
                                } else {
                                    auth.handleOTP(if (auth.emailSend && !auth.emailVerified) "verify" else "send")
                                }
                            }
                        ) {
                            Text(if (auth.emailVerified) "Continue" else "Submit")
                        }
                    }
                }
            }
        }
    }
}
