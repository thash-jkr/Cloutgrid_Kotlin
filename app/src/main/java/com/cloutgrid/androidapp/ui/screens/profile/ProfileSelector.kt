package com.cloutgrid.androidapp.ui.screens.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cloutgrid.androidapp.ui.theme.First
import com.cloutgrid.androidapp.ui.theme.Second
import com.cloutgrid.androidapp.R
import com.cloutgrid.androidapp.ui.theme.OffWhite

@Composable
fun ProfileSelector(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    clip = false
                )
                .background(color = OffWhite, shape = CircleShape)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { title ->
                val isSelected = selectedTab == title

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Second else Color.Transparent)
                        .clickable { onTabSelected(title) }
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (title) {
                        "Posts" -> {
                            Icon(
                                imageVector = Icons.Filled.Grid3x3,
                                contentDescription = "Post",
                                modifier = Modifier.size(25.dp),
                                tint = if (isSelected) Color.White else First
                            )
                        }
                        "Collabs" -> {
                            Icon(
                                imageVector = Icons.Filled.Handshake,
                                contentDescription = "Collab",
                                modifier = Modifier.size(25.dp),
                                tint = if (isSelected) Color.White else First
                            )
                        }
                        "Instagram" -> {
                            Icon(
                                painter = painterResource(id = R.drawable.instagram),
                                contentDescription = "Instagram",
                                tint = if (isSelected) Color.White else First,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                        "YouTube" -> {
                            Icon(
                                painter = painterResource(id = R.drawable.youtube),
                                contentDescription = "YouTube",
                                tint = if (isSelected) Color.White else First,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                        else -> {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}