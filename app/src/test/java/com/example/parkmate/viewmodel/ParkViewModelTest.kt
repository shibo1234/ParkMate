package com.example.parkmate.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParkViewModelTest {
    private val viewModel = ParkViewModel()

    @Test
    fun initialState_exposesAllSeedParks() {
        val state = viewModel.uiState.value

        assertEquals(3, state.parks.size)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun updateSearchQuery_filtersParks() {
        viewModel.updateSearchQuery("geyser")

        val state = viewModel.uiState.value
        assertEquals("geyser", state.searchQuery)
        assertEquals(listOf("yellowstone"), state.parks.map { it.id })
    }

    @Test
    fun selectedParkAndAttraction_followIds() {
        viewModel.selectPark("yosemite")
        viewModel.selectAttraction("mist-trail")

        assertEquals("Yosemite National Park", viewModel.selectedPark.value?.name)
        assertEquals("Mist Trail", viewModel.selectedAttraction.value?.name)

        viewModel.selectAttraction("missing")
        assertNull(viewModel.selectedAttraction.value)
    }
}
