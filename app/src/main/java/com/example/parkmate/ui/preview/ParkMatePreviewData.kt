package com.example.parkmate.ui.preview

import com.example.parkmate.data.seed.ParkSeedData
import com.example.parkmate.viewmodel.ParkUiState

object ParkMatePreviewData {
    val parks = ParkSeedData.parks
    val yosemite = parks.first()
    val tunnelView = yosemite.attractions.first()
    val homeState = ParkUiState(parks = parks, searchQuery = "")
}
