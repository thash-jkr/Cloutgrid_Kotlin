package com.cloutgrid.androidapp.ui.screens.integration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.ui.components.Empty

@Composable
fun OtherYouTube(
    user: UserContainer
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Instagram Insights",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp)
        )

        Empty(
            type = "youtube",
            message = "@${user.profile.username} hasn't connected YouTube yet"
        )
    }
}