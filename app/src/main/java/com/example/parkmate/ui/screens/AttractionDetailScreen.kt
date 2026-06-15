package com.example.parkmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parkmate.data.model.Attraction
import com.example.parkmate.ui.components.ParkMateEmptyState
import com.example.parkmate.ui.components.ParkMateSectionCard
import com.example.parkmate.ui.preview.ParkMatePreviewData
import com.example.parkmate.ui.theme.ParkMateTheme

@Composable
fun AttractionDetailScreen(
    attraction: Attraction?,
    onBack: () -> Unit,
    onUploadClick: () -> Unit
) {
    if (attraction == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onBack) {
                Text("Back")
            }
            ParkMateEmptyState(
                title = "No attraction selected",
                body = "Select an attraction from a park."
            )
        }
        return
    }

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
            AsyncImage(
                model = attraction.imageUrl,
                contentDescription = attraction.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = attraction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item { ParkMateSectionCard(title = "Trail route", body = attraction.trailInfo) }
        item { ParkMateSectionCard(title = "Photo spots", body = attraction.photoTips) }
        item { ParkMateSectionCard(title = "Nearby food", body = attraction.nearbyFood) }
        item { ParkMateSectionCard(title = "Safety tips", body = attraction.safetyTips) }
        item {
            Button(
                onClick = onUploadClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Photo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AttractionDetailScreenPreview() {
    ParkMateTheme {
        AttractionDetailScreen(
            attraction = ParkMatePreviewData.tunnelView,
            onBack = {},
            onUploadClick = {}
        )
    }
}
