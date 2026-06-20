package com.example.parkmate.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.ui.theme.ParkMateTheme

@Composable
fun UploadScreen(
    parkName: String?,
    attractionName: String?,
    isSubmitting: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSubmit: (caption: String, imageUri: Uri?) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) imageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onBack) { Text("Back") }

        Text(
            text = "Create a park post",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        if (parkName != null) {
            Text(
                text = buildString {
                    append(parkName)
                    if (attractionName != null) append(" · $attractionName")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "No photo selected",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedButton(
            onClick = {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting
        ) {
            Text(if (imageUri == null) "Choose Photo" else "Change Photo")
        }

        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Caption") },
            minLines = 3,
            shape = RoundedCornerShape(8.dp),
            enabled = !isSubmitting
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
        }

        Button(
            onClick = { onSubmit(caption, imageUri) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting && imageUri != null && caption.isNotBlank()
        ) {
            Text("Post")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UploadScreenPreview() {
    ParkMateTheme {
        UploadScreen(
            parkName = "Yosemite National Park",
            attractionName = "Half Dome",
            isSubmitting = false,
            errorMessage = null,
            onBack = {},
            onSubmit = { _, _ -> }
        )
    }
}
