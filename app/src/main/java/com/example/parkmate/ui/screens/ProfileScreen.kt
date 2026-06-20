package com.example.parkmate.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.data.model.Park
import com.example.parkmate.data.model.Post
import com.example.parkmate.data.model.UserProfile
import com.example.parkmate.ui.theme.ParkMateTheme

@Composable
fun ProfileScreen(
    user: UserProfile?,
    posts: List<Post>,
    savedParks: List<Park>,
    isUploadingPhoto: Boolean,
    onPhotoSelected: (Uri?) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onPhotoSelected
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Button(onClick = onBack) {
                Text("Back")
            }
        }
        item {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(photoUrl = user?.photoUrl, isUploading = isUploadingPhoto)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user?.displayName?.ifBlank { "ParkMate Traveler" } ?: "Guest Traveler",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = user?.email ?: "Sign in to save parks and manage your posts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (user != null) {
                            OutlinedButton(
                                onClick = {
                                    photoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                enabled = !isUploadingPhoto
                            ) {
                                Text(if (user.photoUrl.isNullOrBlank()) "Add photo" else "Change photo")
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Saved parks (${savedParks.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (savedParks.isEmpty()) {
            item {
                Text(
                    text = "No saved parks yet. Tap Save on a park to keep it here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(savedParks, key = { park -> park.id }) { park ->
                SavedParkCard(park)
            }
        }

        item {
            Text(
                text = "Your posts (${posts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (posts.isEmpty()) {
            item {
                Text(
                    text = "You haven't shared any park photos yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(posts, key = { post -> post.id }) { post ->
                ProfilePostCard(post)
            }
        }
        item {
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Log Out")
            }
        }
    }
}

@Composable
private fun ProfileAvatar(photoUrl: String?, isUploading: Boolean) {
    Card(
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.size(64.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isUploading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                !photoUrl.isNullOrBlank() -> AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
                else -> Text("👤", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun SavedParkCard(park: Park) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = park.imageUrl,
                contentDescription = park.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(park.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    park.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ProfilePostCard(post: Post) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (post.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Your post photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            if (post.caption.isNotBlank()) {
                Text(post.caption, style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                "${post.likeCount} likes · ${post.commentCount} comments",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ParkMateTheme {
        ProfileScreen(
            user = UserProfile(
                id = "preview-user",
                displayName = "Demo Traveler",
                email = "demo@parkmate.app"
            ),
            posts = listOf(
                Post(
                    id = "post-1",
                    userId = "preview-user",
                    caption = "Sunrise at Tunnel View was worth the early alarm.",
                    likeCount = 12,
                    commentCount = 3
                )
            ),
            savedParks = emptyList(),
            isUploadingPhoto = false,
            onPhotoSelected = {},
            onBack = {},
            onLogout = {}
        )
    }
}
