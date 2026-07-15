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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    start = 15.dp,
                    top = innerPadding.calculateTopPadding(),
                    end = 15.dp,
                    bottom = 100.dp
                ),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                itemsIndexed(categories, key = { _, job -> job.id }) { index, category ->
                    val isSelected = category.value == selectedCategory

                    SegmentedListItem(
                        shapes = ListItemDefaults.segmentedShapes(index = index, count = categories.count()),
                        trailingContent = {
                            Icon(
                                imageVector = CategoryList.icon(category.value),
                                contentDescription = category.label,
                                tint = if (isSelected) {
                                    Color.White
                                } else {
                                    Color.Black
                                }
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = OffWhite,
                            selectedContainerColor = Second
                        ),
                        onClick = {
                            onCategorySelected(category)
                        },
                        selected = isSelected
                    ) {
                        Text(
                            category.label,
                            fontWeight = if (isSelected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (isSelected) {
                                Color.White
                            } else {
                                Color.Black
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CategorySheetPreview() {
    CategorySheet(
        categories = CategoryList.allOptions,
        selectedCategory = "Business",
        onCategorySelected = {}
    )
}