package com.example.parkmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.parkmate.data.model.Attraction
import com.example.parkmate.data.model.Park
import com.example.parkmate.data.repository.ParkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ParkUiState(
    val parks: List<Park> = emptyList(),
    val searchQuery: String = ""
)

class ParkViewModel(
    private val repository: ParkRepository = ParkRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParkUiState(parks = repository.getParks()))
    val uiState: StateFlow<ParkUiState> = _uiState.asStateFlow()

    private val _selectedPark = MutableStateFlow<Park?>(null)
    val selectedPark: StateFlow<Park?> = _selectedPark.asStateFlow()

    private val _selectedAttraction = MutableStateFlow<Attraction?>(null)
    val selectedAttraction: StateFlow<Attraction?> = _selectedAttraction.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                parks = repository.searchParks(query)
            )
        }
    }

    fun selectPark(parkId: String) {
        _selectedPark.value = repository.getParkById(parkId)
        _selectedAttraction.value = null
    }

    fun selectAttraction(attractionId: String) {
        _selectedAttraction.value = selectedPark.value?.attractions?.firstOrNull {
            it.id == attractionId
        }
    }
}
