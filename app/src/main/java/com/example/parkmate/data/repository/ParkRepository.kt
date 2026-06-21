package com.example.parkmate.data.repository

import com.example.parkmate.data.model.Park
import com.example.parkmate.data.seed.ParkSeedData

/**
 * What: Serves the National Park guide content (list, lookup by id, search) from local seed data
 * Who:  Used by ParkViewModel and ProfileViewModel to resolve parks
 * When: Read at startup and on every search keystroke
 */
class ParkRepository(

    private val parks: List<Park> = ParkSeedData.parks
) {
    fun getParks(): List<Park> = parks

    fun getParkById(parkId: String): Park? {
        return parks.firstOrNull { it.id == parkId }
    }

    fun searchParks(query: String): List<Park> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isEmpty()) return parks

        return parks.filter { park ->
            park.name.lowercase().contains(normalizedQuery) ||
                park.location.lowercase().contains(normalizedQuery) ||
                park.categories.any { category -> category.lowercase().contains(normalizedQuery) }
        }
    }
}
