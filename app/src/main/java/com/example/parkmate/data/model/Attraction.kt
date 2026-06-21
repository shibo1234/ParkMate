package com.example.parkmate.data.model

/**
 * What: A point of interest within a park, with trail, photo, food, and safety notes
 * Who:  Served as part of a Park by ParkRepository; shown on AttractionDetailScreen
 * When: Loaded at startup; selected via ParkViewModel
 */
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
