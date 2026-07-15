package com.cloutgrid.androidapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cloutgrid.androidapp.ui.theme.Second

@Composable
fun CloutCapsule(
    content: String,
) {
    val categoryLabel = CategoryList.label(content)

    Text(
        text = categoryLabel,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .background(Second, shape = CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}