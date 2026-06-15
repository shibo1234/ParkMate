package com.example.parkmate.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ParkRepositoryTest {
    private val repository = ParkRepository()

    @Test
    fun getParks_returnsThreeSeedParksWithAttractions() {
        val parks = repository.getParks()

        assertEquals(3, parks.size)
        assertTrue(parks.all { it.attractions.size >= 2 })
    }

    @Test
    fun getParkById_returnsMatchingPark() {
        val park = repository.getParkById("yosemite")

        assertNotNull(park)
        assertEquals("Yosemite National Park", park?.name)
    }

    @Test
    fun searchParks_matchesNameLocationAndCategory() {
        assertEquals(listOf("yosemite"), repository.searchParks("California").map { it.id })
        assertEquals(listOf("grand-canyon"), repository.searchParks("family").map { it.id })
        assertEquals(listOf("yellowstone"), repository.searchParks("geyser").map { it.id })
    }
}
