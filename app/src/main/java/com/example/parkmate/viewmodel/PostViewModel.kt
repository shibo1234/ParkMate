package com.example.parkmate.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.parkmate.data.model.Comment
import com.example.parkmate.data.model.Post
import com.example.parkmate.data.model.UserProfile
import com.example.parkmate.data.repository.PostRepository
import com.example.parkmate.post.PostFormValidator
import com.example.parkmate.post.PostLikeReducer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val likedPostIds: Set<String> = emptySet(),
    val caption: String = "",
    val selectedImageUri: Uri? = null,
    val likingPostId: String? = null,
    val activeCommentPostId: String? = null,
    val comments: List<Comment> = emptyList(),
    val commentDraft: String = "",
    val isLoadingComments: Boolean = false,
    val isSubmittingComment: Boolean = false,
    val isLoadingPosts: Boolean = false,
    val isCreatingPost: Boolean = false,
    val errorMessage: String? = null,
    val postCreated: Boolean = false
)

class PostViewModel(
    private val postRepository: PostRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostUiState(isLoadingPosts = true))
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    private var likesJob: Job? = null
    private var likedUserId: String? = null
    private var likedRefreshSignature: String? = null
    private var commentsJob: Job? = null

    init {
        observePosts()
    }

    fun updateCaption(caption: String) {
        _uiState.update {
            it.copy(caption = caption, errorMessage = null, postCreated = false)
        }
    }

    fun updateSelectedImage(uri: Uri?) {
        _uiState.update {
            it.copy(selectedImageUri = uri, errorMessage = null, postCreated = false)
        }
    }

    fun createPost(user: UserProfile?) {
        val state = uiState.value
        val validationError = PostFormValidator.validateCreatePost(
            userId = user?.id.orEmpty(),
            caption = state.caption
        )
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, postCreated = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingPost = true, errorMessage = null, postCreated = false) }
            val result = postRepository.createPost(
                user = requireNotNull(user),
                caption = state.caption,
                imageUri = state.selectedImageUri
            )
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {
                        currentState.copy(
                            caption = "",
                            selectedImageUri = null,
                            isCreatingPost = false,
                            errorMessage = null,
                            postCreated = true
                        )
                    },
                    onFailure = { error ->
                        currentState.copy(isCreatingPost = false, errorMessage = error.message ?: "Post could not be created.")
                    }
                )
            }
        }
    }

    fun consumePostCreated() {
        _uiState.update { it.copy(postCreated = false) }
    }

    fun setCurrentUser(userId: String?) {
        if (userId == likedUserId) return
        likedUserId = userId

        if (userId.isNullOrBlank()) {
            likesJob?.cancel()
            likedRefreshSignature = null
            _uiState.update { it.copy(likedPostIds = emptySet()) }
            return
        }

        refreshLikedPosts()
    }

    private fun refreshLikedPosts() {
        val userId = likedUserId
        if (userId.isNullOrBlank()) return

        val postIds = uiState.value.posts.map { it.id }
        if (postIds.isEmpty()) return

        // Skip redundant refreshes: likeCount-only snapshots keep the same post ids.
        val signature = userId + "|" + postIds.sorted().joinToString(",")
        if (signature == likedRefreshSignature) return
        likedRefreshSignature = signature

        likesJob?.cancel()
        likesJob = viewModelScope.launch {
            postRepository.getLikedPostIds(postIds, userId).onSuccess { likedPostIds ->
                _uiState.update { it.copy(likedPostIds = likedPostIds) }
            }
        }
    }

    fun toggleLike(postId: String, user: UserProfile?) {
        if (postId.isBlank() || uiState.value.likingPostId == postId) return
        if (user == null || user.id.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Sign in to like posts.") }
            return
        }

        val optimistic = PostLikeReducer.toggle(
            posts = uiState.value.posts,
            likedPostIds = uiState.value.likedPostIds,
            postId = postId
        )
        _uiState.update {
            it.copy(
                posts = optimistic.posts,
                likedPostIds = optimistic.likedPostIds,
                likingPostId = postId,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = postRepository.setPostLiked(postId, user.id, optimistic.nowLiked)
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {
                        currentState.copy(likingPostId = null, errorMessage = null)
                    },
                    onFailure = { error ->
                        // Roll back the optimistic change; the live listeners remain the source of truth.
                        val reverted = PostLikeReducer.toggle(
                            posts = currentState.posts,
                            likedPostIds = currentState.likedPostIds,
                            postId = postId
                        )
                        currentState.copy(
                            posts = reverted.posts,
                            likedPostIds = reverted.likedPostIds,
                            likingPostId = null,
                            errorMessage = error.message ?: "Like could not be saved."
                        )
                    }
                )
            }
        }
    }

    fun toggleComments(postId: String) {
        if (postId.isBlank() || uiState.value.activeCommentPostId == postId) {
            closeComments()
            return
        }

        commentsJob?.cancel()
        _uiState.update {
            it.copy(
                activeCommentPostId = postId,
                comments = emptyList(),
                commentDraft = "",
                isLoadingComments = true,
                errorMessage = null
            )
        }

        commentsJob = viewModelScope.launch {
            postRepository.observeComments(postId).collect { result ->
                _uiState.update { current ->
                    if (current.activeCommentPostId != postId) return@update current
                    result.fold(
                        onSuccess = { comments ->
                            current.copy(comments = comments, isLoadingComments = false)
                        },
                        onFailure = { error ->
                            current.copy(
                                isLoadingComments = false,
                                errorMessage = error.message ?: "Comments could not be loaded."
                            )
                        }
                    )
                }
            }
        }
    }

    fun closeComments() {
        commentsJob?.cancel()
        commentsJob = null
        _uiState.update {
            it.copy(
                activeCommentPostId = null,
                comments = emptyList(),
                commentDraft = "",
                isLoadingComments = false
            )
        }
    }

    fun updateCommentDraft(text: String) {
        _uiState.update { it.copy(commentDraft = text, errorMessage = null) }
    }

    fun submitComment(user: UserProfile?) {
        val state = uiState.value
        val postId = state.activeCommentPostId ?: return
        val validationError = PostFormValidator.validateComment(
            userId = user?.id.orEmpty(),
            text = state.commentDraft
        )
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingComment = true, errorMessage = null) }
            val result = postRepository.addComment(
                postId = postId,
                user = requireNotNull(user),
                text = state.commentDraft
            )
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {
                        currentState.copy(commentDraft = "", isSubmittingComment = false, errorMessage = null)
                    },
                    onFailure = { error ->
                        currentState.copy(
                            isSubmittingComment = false,
                            errorMessage = error.message ?: "Comment could not be sent."
                        )
                    }
                )
            }
        }
    }

    private fun observePosts() {
        viewModelScope.launch {
            postRepository.observePosts().collect { result ->
                _uiState.update {
                    result.fold(
                        onSuccess = { posts ->
                            it.copy(posts = posts, isLoadingPosts = false, errorMessage = null)
                        },
                        onFailure = { error ->
                            it.copy(isLoadingPosts = false, errorMessage = error.message ?: "Posts could not be loaded.")
                        }
                    )
                }
                // Once the feed (its post ids) is known, load which of them the user liked.
                if (result.isSuccess) refreshLikedPosts()
            }
        }
    }
}

class PostViewModelFactory(
    private val postRepository: PostRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(postRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
