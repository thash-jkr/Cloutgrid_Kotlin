package com.cloutgrid.androidapp.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cloutgrid.androidapp.ui.theme.First
import com.cloutgrid.androidapp.ui.theme.Second

@Composable
fun LandingScreen(
    onNavigateToBasicInfo: (type: String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                text = "Join ",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                Text(
                    text = "Clout",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = First
                )
                Text(
                    text = "grid",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Second
                )
            }
        }

        HomeSelectionCard(
            text = "I am a Creator",
            onClick = { onNavigateToBasicInfo("Creator") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        HomeSelectionCard(
            text = "I am a Business",
            onClick = { onNavigateToBasicInfo("Business") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {onNavigateToLogin()}
        ) {
            Text(
                text = "Already have an account?",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 10.dp, end = 5.dp)
            )

            Text(
                text = "Login",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 10.dp)
            )
        }
    }
}


@Composable
fun HomeSelectionCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(width = 300.dp, height = 200.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate Forward",
                tint = Color.Black,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LandingScreenPreview() {
//    CloutgridTheme {
//        LandingScreen({}, {})
//    }
//}
