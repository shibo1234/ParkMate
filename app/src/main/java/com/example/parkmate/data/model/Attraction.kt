package com.example.parkmate.data.model

data class Attraction(
    val id: String,
    val parkId: String,
    val name: String,
    val description: String,
    val trailInfo: String,
    val photoTips: String,
    val nearbyFood: String,
    val safetyTips: String,
    val imageUrl: String
)
