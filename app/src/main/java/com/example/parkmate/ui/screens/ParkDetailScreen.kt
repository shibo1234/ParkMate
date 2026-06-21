package com.example.parkmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import com.example.parkmate.data.model.Park
import com.example.parkmate.ui.components.ParkMateEmptyState
import com.example.parkmate.ui.preview.ParkMatePreviewData
import com.example.parkmate.ui.theme.ParkMateTheme

/**
 * What: Park overview with image, description, attractions, and save/unsave
 * Who:  Hosted by ParkMateApp; reads the selected park and saved-park state
 * When: Opened from a Home park card
 */
@Composable
fun ParkDetailScreen(
    park: Park?,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onBack: () -> Unit,
    onAttractionClick: (String) -> Unit,
    onCommunityClick: () -> Unit,
) {
    if (park == null) {
        EmptyDetail(onBack = onBack, message = "Select a park from Home.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) {
                    Text("Back")
                }
                Button(onClick = onCommunityClick) {
                    Text("Community")
                }
            }
        }
        item {
            AsyncImage(
                model = park.imageUrl,
                contentDescription = park.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = park.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = park.location,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = park.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isSaved) {
                    FilledTonalButton(onClick = onSaveToggle) {
                        Text("Saved ✓")
                    }
                } else {
                    Button(onClick = onSaveToggle) {
                        Text("Save park")
                    }
                }
            }
        }
        item {
            Text(
                text = "Featured attractions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        items(park.attractions, key = { it.id }) { attraction ->
            AttractionRow(
                attraction = attraction,
            ) { onAttractionClick(attraction.id) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttractionRow(
    attraction: Attraction,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = attraction.imageUrl,
                contentDescription = attraction.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(92.dp)
                    .weight(0.35f)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier.weight(0.65f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = attraction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyDetail(onBack: () -> Unit, message: String) {
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
            title = "No park selected",
            body = message
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParkDetailScreenPreview() {
    ParkMateTheme {
        ParkDetailScreen(
            park = ParkMatePreviewData.yosemite,
            isSaved = false,
            onSaveToggle = {},
            onBack = {},
            onAttractionClick = {},
        ) { }
    }
}
