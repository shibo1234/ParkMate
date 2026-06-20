package com.example.parkmate.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkmate.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UploadViewModel(
    private val repo : PostRepository = PostRepository()
) : ViewModel() {

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting : StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error : StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success : StateFlow<Boolean> = _success.asStateFlow()

    fun consumeSuccess() { _success.value = false }
    fun clearError() { _error.value = null }

    fun createPost(
        authorId : String,
        authorName : String,
        parkId : String,
        parkName: String,
        attractionName : String?,
        imageUri : Uri?,
        caption : String
    ) {
        when {
            authorId.isBlank() -> { _error.value = "Please sign in to upload a post."; return }
            imageUri == null -> { _error.value = "Please select an image to upload."; return }
            caption.isBlank() -> { _error.value = "Please enter a caption."; return }
        }
        _error.value = null
        viewModelScope.launch {
            _isSubmitting.value = true
            repo.createPost(
                authorId = authorId,
                authorName = authorName,
                parkId = parkId,
                parkName = parkName,
                attractionName = attractionName,
                imageUrl = imageUri,
                caption = caption
            )
                .onSuccess { _success.value = true }
                .onFailure { _error.value = it.localizedMessage ?: "Upload failed. Please try again later."}
            _isSubmitting.value = false
        }
    }
}