package com.cloutgrid.androidapp.ui.screens.auth.register

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.cloutgrid.androidapp.data.model.HeaderAction
import com.cloutgrid.androidapp.models.CategoryList
import com.cloutgrid.androidapp.ui.components.CategorySheet
import com.cloutgrid.androidapp.ui.components.CloutHeader
import com.cloutgrid.androidapp.ui.components.CloutSheet
import com.cloutgrid.androidapp.ui.screens.auth.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreInfoScreen(onNavigateBack: () -> Unit, auth: AuthManager, onNavigateToLogin: () -> Unit) {
    var showCategorySheet by remember { mutableStateOf(false) }
    val confirmPassword = rememberTextFieldState()

    val type = auth.selectedType
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        auth.events.collect { success ->
            if (success) {
                Toast.makeText(context, auth.successMessage, Toast.LENGTH_LONG).show()
                onNavigateToLogin()
            } else {
                Toast.makeText(context, auth.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
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
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedSecureTextField(
                    state = auth.password,
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                OutlinedSecureTextField(
                    state = confirmPassword,
                    label = { Text("Confirm Password") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showCategorySheet = true
                        }
                ) {
                    OutlinedTextField(
                        value = CategoryList.label(auth.category),
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button({
                        if (auth.password.text.toString() == confirmPassword.text.toString()) {
                            auth.handleRegister(type)
                        } else {
                            Toast.makeText(
                                context,
                                "Passwords do not match",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }) {
                        Text("Submit")
                    }
                }

                ConsentText()
            }
        }

        if (showCategorySheet) {
            CloutSheet(
                { showCategorySheet = false }
            ) {
                CategorySheet(CategoryList.allOptions, auth.category) { cat ->
                    auth.category = cat.value
                    showCategorySheet = false
                }
            }
        }
    }
}

@Composable
fun ConsentText() {
    val linkStyle = TextLinkStyles(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    )

    val annotatedText = buildAnnotatedString {
        append("By clicking submit, you agree to our ")

        withLink(
            LinkAnnotation.Url(
                url = "https://cloutgrid.com/privacypolicy",
                styles = linkStyle
            )
        ) {
            append("Privacy Policy")
        }

        append(" and ")

        withLink(
            LinkAnnotation.Url(
                url = "https://cloutgrid.com/eula",
                styles = linkStyle
            )
        ) {
            append("End User Licence Agreement (EULA)")
        }

        append(".")
    }

    Text(text = annotatedText)
}