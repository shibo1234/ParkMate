package com.example.parkmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parkmate.data.model.Park
import com.example.parkmate.data.repository.ParkRepository
import com.example.parkmate.data.repository.SavedParkRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** What/Who/When: UI state for the profile's saved parks. Read by ProfileScreen and ParkDetailScreen. */
data class ProfileUiState(
    val savedParkIds: Set<String> = emptySet(),
    val savedParks: List<Park> = emptyList(),
    val errorMessage: String? = null
)

/**
 * What: Streams the signed-in user's saved parks and exposes save/unsave with optimistic updates
 * Who:  Drives ProfileScreen and the save toggle on ParkDetailScreen; uses SavedParkRepository
 * When: Re-subscribes whenever the current user changes
 */
class ProfileViewModel(
    private val savedParkRepository: SavedParkRepository,
    private val parkRepository: ParkRepository = ParkRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var savedParksJob: Job? = null
    private var currentUserId: String? = null

    fun setCurrentUser(userId: String?) {
        if (userId == currentUserId) return
        currentUserId = userId
        savedParksJob?.cancel()

        if (userId.isNullOrBlank()) {
            _uiState.update { it.copy(savedParkIds = emptySet(), savedParks = emptyList()) }
            return
        }

        savedParksJob = viewModelScope.launch {
            savedParkRepository.observeSavedParkIds(userId).collect { result ->
                result.onSuccess { ids ->
                    _uiState.update { it.copy(savedParkIds = ids, savedParks = resolveParks(ids)) }
                }
            }
        }
    }

    fun isParkSaved(parkId: String): Boolean = parkId in uiState.value.savedParkIds

    /** What/Who/When: Optimistically saves/unsaves a park, then persists it; rolls back on failure. */
    fun toggleSavedPark(parkId: String, userId: String?) {
        if (parkId.isBlank()) return
        if (userId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Sign in to save parks.") }
            return
        }

        val nowSaved = parkId !in uiState.value.savedParkIds
        // Optimistic update; the live listener reconciles with the server afterwards.
        val optimisticIds = if (nowSaved) {
            uiState.value.savedParkIds + parkId
        } else {
            uiState.value.savedParkIds - parkId
        }
        _uiState.update {
            it.copy(savedParkIds = optimisticIds, savedParks = resolveParks(optimisticIds), errorMessage = null)
        }

        viewModelScope.launch {
            savedParkRepository.setParkSaved(userId, parkId, nowSaved).onFailure { error ->
                val revertedIds = if (nowSaved) {
                    uiState.value.savedParkIds - parkId
                } else {
                    uiState.value.savedParkIds + parkId
                }
                _uiState.update {
                    it.copy(
                        savedParkIds = revertedIds,
                        savedParks = resolveParks(revertedIds),
                        errorMessage = error.message ?: "Could not update saved parks."
                    )
                }
            }
        }
    }

    private fun resolveParks(ids: Set<String>): List<Park> {
        return ids.mapNotNull { parkRepository.getParkById(it) }.sortedBy { it.name }
    }
}

/** What/Who/When: Builds ProfileViewModel with the chosen SavedParkRepository. Used by MainActivity. */
class ProfileViewModelFactory(
    private val savedParkRepository: SavedParkRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(savedParkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
