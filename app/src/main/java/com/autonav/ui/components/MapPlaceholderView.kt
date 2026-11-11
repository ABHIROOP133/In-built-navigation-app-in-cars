package com.autonav.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autonav.R
import com.autonav.ui.theme.CharcoalBackground
import com.autonav.ui.theme.CharcoalSurface
import com.autonav.ui.theme.CharcoalSurfaceElevated
import com.autonav.ui.theme.GridHairline
import com.autonav.ui.theme.SteelBlueAccent
import com.autonav.ui.theme.StructuralBorder
import com.autonav.ui.theme.TextPrimary
import com.autonav.ui.theme.TextSecondary

/**
 * Primary map viewport placeholder occupying the central area of the AutoNav home screen.
 *
 * Uses a restrained technical grid and explicitly communicates that Google Maps SDK
 * integration and real-time GPS rendering are pending subsequent milestones.
 * Never displays simulated coordinates, speed, or fake navigation routes.
 */
@Composable
fun MapPlaceholderView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground, RoundedCornerShape(4.dp))
            .border(width = 1.dp, color = StructuralBorder, shape = RoundedCornerShape(4.dp))
            .testTag("map_placeholder_view"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle technical orthographic grid for viewport framing
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepPx = 64.dp.toPx()
            val width = size.width
            val height = size.height

            var x = stepPx
            while (x < width) {
                drawLine(
                    color = GridHairline,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += stepPx
            }

            var y = stepPx
            while (y < height) {
                drawLine(
                    color = GridHairline,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += stepPx
            }

            // Center calibration reticle
            val centerX = width / 2f
            val centerY = height / 2f
            val reticleArm = 20.dp.toPx()

            drawLine(
                color = StructuralBorder,
                start = Offset(centerX - reticleArm, centerY),
                end = Offset(centerX + reticleArm, centerY),
                strokeWidth = 1.5f
            )
            drawLine(
                color = StructuralBorder,
                start = Offset(centerX, centerY - reticleArm),
                end = Offset(centerX, centerY + reticleArm),
                strokeWidth = 1.5f
            )
        }

        // Restrained central engineering notice panel
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .padding(24.dp)
                .background(CharcoalSurface, RoundedCornerShape(4.dp))
                .border(width = 1.dp, color = StructuralBorder, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(CharcoalSurfaceElevated, RoundedCornerShape(2.dp))
                    .border(width = 1.dp, color = SteelBlueAccent, shape = RoundedCornerShape(2.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.map_placeholder_badge),
                    style = MaterialTheme.typography.labelMedium,
                    color = SteelBlueAccent
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(id = R.string.map_placeholder_title),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.map_placeholder_description),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
