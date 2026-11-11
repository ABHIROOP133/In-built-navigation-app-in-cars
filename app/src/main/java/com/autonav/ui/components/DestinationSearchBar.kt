package com.autonav.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.autonav.R
import com.autonav.ui.theme.CharcoalSurface
import com.autonav.ui.theme.CharcoalSurfaceElevated
import com.autonav.ui.theme.StatusDisconnectedTone
import com.autonav.ui.theme.SteelBlueAccent
import com.autonav.ui.theme.StructuralBorder
import com.autonav.ui.theme.TextMuted
import com.autonav.ui.theme.TextPrimary
import com.autonav.ui.theme.TextSecondary

/**
 * Automotive destination search bar with touch-sized input field, search icon,
 * and explicit feedback when search is invoked during the Day 1 foundation milestone.
 */
@Composable
fun DestinationSearchBar(
    query: String,
    pendingFeatureMessage: String?,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearQuery: () -> Unit,
    onDismissPendingMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("destination_search_container"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .testTag("destination_search_input"),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SteelBlueAccent
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = onClearQuery,
                            modifier = Modifier.testTag("destination_search_clear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.search_clear_description),
                                tint = TextSecondary
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        onSearchSubmit()
                    }
                ),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CharcoalSurface,
                    unfocusedContainerColor = CharcoalSurface,
                    focusedBorderColor = SteelBlueAccent,
                    unfocusedBorderColor = StructuralBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = SteelBlueAccent
                )
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSearchSubmit()
                },
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .testTag("destination_search_submit_button"),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CharcoalSurfaceElevated,
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = SteelBlueAccent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.search_submit_label),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (pendingFeatureMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CharcoalSurfaceElevated, RoundedCornerShape(4.dp))
                    .border(1.dp, StructuralBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_pending_notice"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pendingFeatureMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StatusDisconnectedTone,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismissPendingMessage) {
                    Text(
                        text = "Dismiss",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
