package com.autonav.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.autonav.ui.screens.HomeScreen
import com.autonav.ui.theme.AutoNavTheme
import com.autonav.viewmodel.HomeViewModel
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysAllRequiredDay1SectionsAndTruthfulStatus() {
        composeTestRule.setContent {
            AutoNavTheme {
                HomeScreen(viewModel = HomeViewModel())
            }
        }

        // Verify Header, Search, Map Placeholder, and Bottom Status Bar render
        composeTestRule.onNodeWithTag("top_header_bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("AUTONAV").assertIsDisplayed()
        composeTestRule.onNodeWithTag("destination_search_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("map_placeholder_view").assertIsDisplayed()
        composeTestRule.onNodeWithText("MAP ENGINE PENDING").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottom_status_bar").assertIsDisplayed()

        // Verify truthful Day 1 telemetry values
        composeTestRule.onNodeWithText("Not connected").assertIsDisplayed()
        composeTestRule.onNodeWithText("Inactive").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ready").assertIsDisplayed()
    }

    @Test
    fun homeScreen_clickingSettingsOpensPendingDialog() {
        composeTestRule.setContent {
            AutoNavTheme {
                HomeScreen(viewModel = HomeViewModel())
            }
        }

        composeTestRule.onNodeWithTag("settings_button").performClick()
        composeTestRule.onNodeWithTag("settings_pending_dialog").assertIsDisplayed()
    }
}
