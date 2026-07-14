package com.cloutgrid.androidapp.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.OutlinedSecureTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.ui.components.CloutHeader

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    authManager: AuthManager = hiltViewModel(),
    onNavigateToResetPassword: () -> Unit
) {
    var type by remember { mutableStateOf("Creator") }
    val isCreator = type == "Creator"

    val context = LocalContext.current

    val emailState = rememberTextFieldState()
    val passwordState = rememberTextFieldState()

    LaunchedEffect(Unit) {
        authManager.events.collect { success ->
            if (!success) {
                Toast.makeText(context, authManager.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "$type Login",
                icon = HeaderAction(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    onClick = { onNavigateBack() }
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    state = emailState,
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all=10.dp)
                )

                OutlinedSecureTextField(
                    state = passwordState,
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all=10.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (authManager.isLoading) {
                        LoadingIndicator()
                    } else {
                        Button({
                            val email = emailState.text.toString().trim()
                            val password = passwordState.text.toString().trim()

                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                authManager.login(email, password, type.lowercase())
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill in all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text("Submit")
                        }
                    }
                }

                Column {
                    LoginFooter(
                        question = "Don't have an account?",
                        answer = "Register",
                        onClick = {
                            onNavigateBack()
                        }
                    )

                    LoginFooter(
                        question = if (isCreator) "Not a Creator?" else "Not a Business?",
                        answer = if (isCreator) "Business Login" else "Creator Login",
                        onClick = { type = if (isCreator) "Business" else "Creator" }
                    )

                    LoginFooter(
                        question = "Forgot Password?",
                        answer = "Reset",
                        onClick = {
                            onNavigateToResetPassword()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginFooter(question: String, answer: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = question,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 10.dp, end = 5.dp)
        )

        Text(
            text = answer,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 10.dp)
        )
    }
}