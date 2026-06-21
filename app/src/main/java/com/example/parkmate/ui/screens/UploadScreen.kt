package com.example.parkmate.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.ui.theme.ParkMateTheme
import com.example.parkmate.viewmodel.PostUiState

/**
 * What: Photo-picker and caption form for creating a new community post
 * Who:  Hosted by ParkMateApp; drives PostViewModel.createPost via PostUiState
 * When: Opened from an attraction or the Community screen
 */
@Composable
fun UploadScreen(
    state: PostUiState,
    onCaptionChange: (String) -> Unit,
    onImageSelected: (android.net.Uri?) -> Unit,
    onBack: () -> Unit,
    onCreatePost: () -> Unit
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onImageSelected
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }
        Text(
            text = "Create a park post",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Share a trip photo and note with the park community.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isCreatingPost
        ) {
            Text(if (state.selectedImageUri == null) "Choose Photo" else "Change Photo")
        }
        state.selectedImageUri?.let { imageUri ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected post photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        OutlinedTextField(
            value = state.caption,
            onValueChange = onCaptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Caption") },
            minLines = 3,
            shape = RoundedCornerShape(8.dp)
        )
        state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        Button(
            onClick = onCreatePost,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isCreatingPost && state.caption.isNotBlank()
        ) {
            if (state.isCreatingPost) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Create Post")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadScreenPreview() {
    ParkMateTheme {
        UploadScreen(
            state = PostUiState(caption = "Sunrise at Tunnel View was worth the early alarm."),
            onCaptionChange = {},
            onImageSelected = {},
            onBack = {},
            onCreatePost = {}
        )
    }
}
