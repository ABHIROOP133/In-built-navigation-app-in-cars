package com.autonav.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.autonav.R
import com.autonav.ui.theme.CharcoalSurface
import com.autonav.ui.theme.SteelBlueAccent
import com.autonav.ui.theme.StructuralBorder
import com.autonav.ui.theme.TextPrimary
import com.autonav.ui.theme.TextSecondary
import com.autonav.utils.AutomotiveClock
import kotlinx.coroutines.delay
import java.time.LocalDateTime

/**
 * Top automotive infotainment bar displaying AutoNav branding, live system clock/date,
 * and a touch-sized Settings button.
 */
@Composable
fun TopHeaderBar(
    onSettingsClick: () -> Unit,
    showExtendedSubtitle: Boolean = true,
    modifier: Modifier = Modifier
) {
    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = LocalDateTime.now()
            delay(15_000L)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CharcoalSurface)
            .border(width = 1.dp, color = StructuralBorder)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .testTag("top_header_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Brand Identity
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(28.dp)
                    .background(SteelBlueAccent)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.header_brand_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                if (showExtendedSubtitle) {
                    Text(
                        text = stringResource(id = R.string.header_brand_subtitle),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        // Right cluster: Live Date/Time + Settings control
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = if (showExtendedSubtitle) {
                    AutomotiveClock.formatHeaderTimestamp(currentDateTime)
                } else {
                    AutomotiveClock.formatTime(currentDateTime)
                },
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.testTag("header_clock_text")
            )

            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("settings_button"),
                shape = RoundedCornerShape(4.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = SolidColor(StructuralBorder)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CharcoalSurface,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.header_settings_button),
                    tint = SteelBlueAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.header_settings_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
