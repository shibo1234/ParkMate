package com.example.parkmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.data.model.Comment
import com.example.parkmate.data.model.Post
import com.example.parkmate.ui.theme.ParkMateTheme
import com.example.parkmate.viewmodel.PostUiState

@Composable
fun CommunityScreen(
    state: PostUiState,
    onBack: () -> Unit,
    onUploadClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onToggleComments: (String) -> Unit,
    onCommentDraftChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) {
                    Text("Back")
                }
                Button(onClick = onUploadClick) {
                    Text("Upload")
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Community",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Recent notes from ParkMate travelers.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        state.errorMessage?.let { errorMessage ->
            item {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        if (state.isLoadingPosts && state.posts.isEmpty()) {
            item {
                CircularProgressIndicator()
            }
        } else if (state.posts.isEmpty()) {
            item {
                Text(
                    text = "No posts yet. Create the first community note.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(state.posts, key = { post -> post.id }) { post ->
                val commentsOpen = state.activeCommentPostId == post.id
                PostCard(
                    post = post,
                    isLiked = post.id in state.likedPostIds,
                    isLiking = state.likingPostId == post.id,
                    onLikeClick = onLikeClick,
                    isCommentsOpen = commentsOpen,
                    comments = if (commentsOpen) state.comments else emptyList(),
                    commentDraft = if (commentsOpen) state.commentDraft else "",
                    isLoadingComments = commentsOpen && state.isLoadingComments,
                    isSubmittingComment = commentsOpen && state.isSubmittingComment,
                    onToggleComments = onToggleComments,
                    onCommentDraftChange = onCommentDraftChange,
                    onSubmitComment = onSubmitComment
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview() {
    ParkMateTheme {
        CommunityScreen(
            state = PostUiState(
                posts = listOf(
                    Post(
                        id = "post-1",
                        userName = "Catherine",
                        parkId = "yosemite",
                        imageUrl = "https://images.unsplash.com/photo-1472396961693-142e6e269027",
                        caption = "Sunrise at Tunnel View was worth the early alarm.",
                        likeCount = 12,
                        commentCount = 3
                    ),
                    Post(
                        id = "post-2",
                        userName = "Xuewen",
                        parkId = "grand-canyon",
                        caption = "Mather Point has the easiest access for first-time visitors.",
                        likeCount = 8,
                        commentCount = 2
                    )
                ),
                likedPostIds = setOf("post-1"),
                activeCommentPostId = "post-1",
                comments = listOf(
                    Comment(id = "c1", userName = "Mara", text = "Adding this to my sunrise list!"),
                    Comment(id = "c2", userName = "Devon", text = "Which parking lot did you use?")
                )
            ),
            onBack = {},
            onUploadClick = {},
            onLikeClick = {},
            onToggleComments = {},
            onCommentDraftChange = {},
            onSubmitComment = {}
        )
    }
}

@Composable
private fun PostCard(
    post: Post,
    isLiked: Boolean,
    isLiking: Boolean,
    onLikeClick: (String) -> Unit,
    isCommentsOpen: Boolean,
    comments: List<Comment>,
    commentDraft: String,
    isLoadingComments: Boolean,
    isSubmittingComment: Boolean,
    onToggleComments: (String) -> Unit,
    onCommentDraftChange: (String) -> Unit,
    onSubmitComment: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(post.userName.ifBlank { "ParkMate Traveler" }, fontWeight = FontWeight.SemiBold)
                Text(post.parkId.toParkLabel(), color = MaterialTheme.colorScheme.primary)
            }
            if (post.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Community post photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(post.caption, style = MaterialTheme.typography.bodyLarge)
            Text(
                "${post.likeCount} likes · ${post.commentCount} comments",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = { onToggleComments(post.id) }) {
                    Text(if (isCommentsOpen) "Hide comments" else "Comments")
                }
                val likeLabel = when {
                    isLiking -> "Saving"
                    isLiked -> "Unlike"
                    else -> "Like"
                }
                if (isLiked) {
                    FilledTonalButton(
                        onClick = { onLikeClick(post.id) },
                        enabled = !isLiking
                    ) {
                        Text(likeLabel)
                    }
                } else {
                    Button(
                        onClick = { onLikeClick(post.id) },
                        enabled = !isLiking
                    ) {
                        Text(likeLabel)
                    }
                }
            }
            if (isCommentsOpen) {
                CommentSection(
                    comments = comments,
                    commentDraft = commentDraft,
                    isLoadingComments = isLoadingComments,
                    isSubmittingComment = isSubmittingComment,
                    onCommentDraftChange = onCommentDraftChange,
                    onSubmitComment = onSubmitComment
                )
            }
        }
    }
}

@Composable
private fun CommentSection(
    comments: List<Comment>,
    commentDraft: String,
    isLoadingComments: Boolean,
    isSubmittingComment: Boolean,
    onCommentDraftChange: (String) -> Unit,
    onSubmitComment: () -> Unit
) {
    HorizontalDivider()
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        when {
            isLoadingComments -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
            comments.isEmpty() -> Text(
                text = "No comments yet. Start the conversation.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> comments.forEach { comment -> CommentRow(comment) }
        }
        OutlinedTextField(
            value = commentDraft,
            onValueChange = onCommentDraftChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Add a comment") },
            enabled = !isSubmittingComment,
            shape = RoundedCornerShape(8.dp)
        )
        Button(
            onClick = onSubmitComment,
            modifier = Modifier.align(Alignment.End),
            enabled = !isSubmittingComment && commentDraft.isNotBlank()
        ) {
            if (isSubmittingComment) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
            } else {
                Text("Send")
            }
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            comment.userName.ifBlank { "ParkMate Traveler" },
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(comment.text, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun String.toParkLabel(): String {
    return when (this) {
        "yosemite" -> "Yosemite"
        "yellowstone" -> "Yellowstone"
        "grand-canyon" -> "Grand Canyon"
        else -> "Park"
    }
}
