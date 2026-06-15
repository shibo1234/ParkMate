package com.example.parkmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.parkmate.ui.theme.ParkMateTheme

@Composable
fun CommunityScreen(
    onBack: () -> Unit,
    onUploadClick: () -> Unit,
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
                    text = "Firebase posts will appear here after upload is connected.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            DemoPostCard(
                user = "Catherine",
                park = "Yosemite",
                caption = "Sunrise at Tunnel View was worth the early alarm.",
                stats = "12 likes · 3 comments"
            )
        }
        item {
            DemoPostCard(
                user = "Xuewen",
                park = "Grand Canyon",
                caption = "Mather Point has the easiest access for first-time visitors.",
                stats = "8 likes · 2 comments"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview() {
    ParkMateTheme {
        CommunityScreen(onBack = {}) { }
    }
}

@Composable
private fun DemoPostCard(
    user: String,
    park: String,
    caption: String,
    stats: String
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
                Text(user, fontWeight = FontWeight.SemiBold)
                Text(park, color = MaterialTheme.colorScheme.primary)
            }
            Text(caption, style = MaterialTheme.typography.bodyLarge)
            Text(
                stats,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
