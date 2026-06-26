package com.cloutgrid.androidapp.models

import java.util.UUID

data class CategoryList(
    val id: String = UUID.randomUUID().toString(),
    val value: String,
    val label: String
) {
    companion object {
        fun label(value: String): String {
            return allOptions.find { it.value == value }?.label ?: ""
        }

        val allOptions: List<CategoryList> = listOf(
            CategoryList(value = "art", label = "Art and Photography"),
            CategoryList(value = "automotive", label = "Automotive"),
            CategoryList(value = "beauty", label = "Beauty and Makeup"),
            CategoryList(value = "business", label = "Business"),
            CategoryList(value = "diversity", label = "Diversity and Inclusion"),
            CategoryList(value = "education", label = "Education"),
            CategoryList(value = "entertainment", label = "Entertainment"),
            CategoryList(value = "fashion", label = "Fashion"),
            CategoryList(value = "finance", label = "Finance"),
            CategoryList(value = "food", label = "Food and Beverage"),
            CategoryList(value = "gaming", label = "Gaming"),
            CategoryList(value = "health", label = "Health and Wellness"),
            CategoryList(value = "home", label = "Home and Gardening"),
            CategoryList(value = "outdoor", label = "Outdoor and Nature"),
            CategoryList(value = "parenting", label = "Parenting and Family"),
            CategoryList(value = "pets", label = "Pets"),
            CategoryList(value = "sports", label = "Sports and Fitness"),
            CategoryList(value = "technology", label = "Technology"),
            CategoryList(value = "travel", label = "Travel"),
            CategoryList(value = "videography", label = "Videography")
        )
    }
}