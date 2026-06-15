package com.example.parkmate.data.model

data class Park(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val imageUrl: String,
    val categories: List<String>,
    val attractions: List<Attraction>
)
