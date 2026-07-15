package com.cloutgrid.androidapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Diversity3
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SportsBasketball
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Theaters
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Yard
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

data class CategoryList(
    val id: String = UUID.randomUUID().toString(),
    val value: String,
    val label: String,
    val icon: ImageVector
) {
    companion object {
        fun label(value: String): String {
            return allOptions.find { it.value == value }?.label ?: ""
        }

        fun icon(value: String): ImageVector {
            return allOptions.find { it.value == value }?.icon ?: Icons.Rounded.Category
        }

        val allOptions: List<CategoryList> = listOf(
            CategoryList(value = "art", label = "Art and Photography", icon = Icons.Rounded.Palette),
            CategoryList(value = "automotive", label = "Automotive", icon = Icons.Rounded.DirectionsCar),
            CategoryList(value = "beauty", label = "Beauty and Makeup", icon = Icons.Rounded.Face),
            CategoryList(value = "business", label = "Business", icon = Icons.Rounded.BusinessCenter),
            CategoryList(value = "diversity", label = "Diversity and Inclusion", icon = Icons.Rounded.Diversity3),
            CategoryList(value = "education", label = "Education", icon = Icons.Rounded.School),
            CategoryList(value = "entertainment", label = "Entertainment", icon = Icons.Rounded.Theaters),
            CategoryList(value = "fashion", label = "Fashion", icon = Icons.Rounded.Checkroom),
            CategoryList(value = "finance", label = "Finance", icon = Icons.Rounded.AttachMoney),
            CategoryList(value = "food", label = "Food and Beverage", icon = Icons.Rounded.Restaurant),
            CategoryList(value = "gaming", label = "Gaming", icon = Icons.Rounded.SportsEsports),
            CategoryList(value = "health", label = "Health and Wellness", icon = Icons.Rounded.Favorite),
            CategoryList(value = "home", label = "Home and Gardening", icon = Icons.Rounded.Yard),
            CategoryList(value = "outdoor", label = "Outdoor and Nature", icon = Icons.Rounded.Park),
            CategoryList(value = "parenting", label = "Parenting and Family", icon = Icons.Rounded.FamilyRestroom),
            CategoryList(value = "pets", label = "Pets", icon = Icons.Rounded.Pets),
            CategoryList(value = "sports", label = "Sports and Fitness", icon = Icons.Rounded.SportsBasketball),
            CategoryList(value = "technology", label = "Technology", icon = Icons.Rounded.Computer),
            CategoryList(value = "travel", label = "Travel", icon = Icons.Rounded.Flight),
            CategoryList(value = "videography", label = "Videography", icon = Icons.Rounded.Videocam)
        )
    }
}