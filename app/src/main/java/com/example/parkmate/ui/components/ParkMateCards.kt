package com.example.parkmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * What: Reusable titled card for a labeled block of text
 * Who:  Used by AttractionDetailScreen (trail/food/safety sections) and ParkMateEmptyState
 * When: Rendered wherever a titled info card is needed
 */
@Composable
fun ParkMateSectionCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * What: Reusable empty-state card (a styled SectionCard) for "nothing here yet" messages.
 * Who:  Used by ParkDetailScreen and AttractionDetailScreen when no item is selected.
 * When: Rendered when a screen has no content to show.
 */
@Composable
fun ParkMateEmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    ParkMateSectionCard(
        title = title,
        body = body,
        modifier = modifier
    )
}
