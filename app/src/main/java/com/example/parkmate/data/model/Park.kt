package com.example.parkmate.data.model

/**
 * What: A National Park and its attractions shown in the guide
 * Who:  Served by ParkRepository from local seed data; read by the park screens
 * When: Loaded at startup; held in ParkViewModel
 */
data class Park(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val imageUrl: String,
    val categories: List<String>,
    val attractions: List<Attraction>
)
