package com.autonav.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.autonav.data.model.Destination
import com.autonav.data.model.DestinationCategory
import com.autonav.ui.theme.CharcoalSurface
import com.autonav.ui.theme.CharcoalSurfaceElevated
import com.autonav.ui.theme.SteelBlueAccent
import com.autonav.ui.theme.StructuralBorder
import com.autonav.ui.theme.TextPrimary
import com.autonav.ui.theme.TextSecondary

/**
 * Automotive search results overlay offering arm's-length touch targets (min 64dp row height),
 * high-contrast typography, and category icons for in-cabin glanceability.
 */
@Composable
fun SearchResultsOverlay(
    results: List<Destination>,
    onSelectDestination: (Destination) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 380.dp)
            .background(CharcoalSurface, RoundedCornerShape(4.dp))
            .border(width = 1.dp, color = StructuralBorder, shape = RoundedCornerShape(4.dp))
            .testTag("search_results_overlay")
    ) {
        // Header bar with result count & close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CharcoalSurfaceElevated)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MATCHING DESTINATIONS (${results.size})",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("close_search_results_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close search results",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Divider(color = StructuralBorder, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(results, key = { it.id }) { destination ->
                SearchResultRow(
                    destination = destination,
                    onClick = { onSelectDestination(destination) }
                )
                Divider(color = StructuralBorder, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    destination: Destination,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("search_result_item_${destination.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Icon in subtle circular badge
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(CharcoalSurfaceElevated, CircleShape)
                .border(1.dp, StructuralBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon(destination.category),
                contentDescription = destination.category.displayLabel,
                tint = SteelBlueAccent,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and Address / Subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = destination.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = destination.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Distance Badge
        if (destination.formattedDistance.isNotBlank()) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .background(CharcoalSurfaceElevated, RoundedCornerShape(2.dp))
                    .border(1.dp, StructuralBorder, RoundedCornerShape(2.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = destination.formattedDistance,
                    style = MaterialTheme.typography.labelMedium,
                    color = SteelBlueAccent
                )
            }
        }
    }
}

private fun categoryIcon(category: DestinationCategory): ImageVector = when (category) {
    DestinationCategory.AIRPORT -> Icons.Default.Flight
    DestinationCategory.TRANSIT -> Icons.Default.Train
    DestinationCategory.CHARGING_STATION -> Icons.Default.EvStation
    DestinationCategory.FUEL -> Icons.Default.LocalGasStation
    DestinationCategory.HOSPITAL -> Icons.Default.LocalHospital
    else -> Icons.Default.LocationOn
}

