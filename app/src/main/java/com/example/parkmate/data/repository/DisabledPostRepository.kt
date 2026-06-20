package com.example.parkmate.data.repository

import android.net.Uri
import com.example.parkmate.data.model.Comment
import com.example.parkmate.data.model.Post
import com.example.parkmate.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DisabledPostRepository : PostRepository {
    override fun observePosts(): Flow<Result<List<Post>>> {
        return flowOf(Result.failure(IllegalStateException(SETUP_MESSAGE)))
    }

    override suspend fun createPost(
        user: UserProfile,
        caption: String,
        imageUri: Uri?,
        parkId: String,
        attractionId: String?
    ): Result<Unit> {
        return Result.failure(IllegalStateException(SETUP_MESSAGE))
    }

    override suspend fun getLikedPostIds(
        postIds: List<String>,
        userId: String
    ): Result<Set<String>> {
        return Result.success(emptySet())
    }

    override suspend fun setPostLiked(
        postId: String,
        userId: String,
        liked: Boolean
    ): Result<Unit> {
        return Result.failure(IllegalStateException(SETUP_MESSAGE))
    }

    override fun observeComments(postId: String): Flow<Result<List<Comment>>> {
        return flowOf(Result.success(emptyList()))
    }

    override suspend fun addComment(
        postId: String,
        user: UserProfile,
        text: String
    ): Result<Unit> {
        return Result.failure(IllegalStateException(SETUP_MESSAGE))
    }

    private companion object {
        const val SETUP_MESSAGE = "Firebase is not configured yet. Add app/google-services.json to load community posts."
    }
}
