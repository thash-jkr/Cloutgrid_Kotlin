package com.cloutgrid.androidapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cloutgrid.androidapp.models.CategoryList
import com.cloutgrid.androidapp.ui.theme.OffWhite
import com.cloutgrid.androidapp.ui.theme.Second

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySheet(
    categories: List<CategoryList>,
    selectedCategory: String = "",
    onCategorySelected: (CategoryList) -> Unit,
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CloutHeader(
                title = "Choose Category",
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = innerPadding.calculateTopPadding(),
                    end = 16.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            OffWhite
                        )
                    ) {
                        categories.forEachIndexed { index, category ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.label,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onCategorySelected(category) }
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                    fontWeight = if (category.value == selectedCategory) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    },
                                    color = if (category.value == selectedCategory) {
                                        Second
                                    } else {
                                        Color.Black
                                    },
                                )

                                if (category.value == selectedCategory) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = "Checkmark",
                                        modifier = Modifier
                                            .padding(end = 20.dp)
                                            .size(25.dp),
                                        tint = Second
                                    )
                                }
                            }

                            if (index < categories.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}