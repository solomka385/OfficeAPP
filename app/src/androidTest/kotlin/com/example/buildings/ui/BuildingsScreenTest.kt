package com.example.buildings.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buildings.presentation.buildings.BuildingsScreen
import com.example.buildings.utils.TestViewModelFactory
import com.example.core.domain.model.Building
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.buildings.presentation.buildings.BuildingsViewModel

@RunWith(AndroidJUnit4::class)
class BuildingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenNoBuildings_showsEmptyMessage() {
        // Arrange
        val factory = TestViewModelFactory()
        val viewModel = factory.create(BuildingsViewModel::class.java)

        // Act
        composeTestRule.setContent {
            BuildingsScreen(
                onBuildingClick = {},
                onAddBuilding = {},
                onShowTverskaya = {},
                viewModel = viewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Здания").assertIsDisplayed()
        composeTestRule.onNodeWithText("Нет зданий").assertIsDisplayed()
    }

    @Test
    fun whenBuildingsExist_showsList() {
        // Arrange
        val factory = TestViewModelFactory()
        val building1 = Building.BuildingWithoutParking("1", "Тверская 10")
        val building2 = Building.BuildingWithParking("2", "Арбат 20", 5, 100.0)
        factory.addTestBuildings(building1, building2)
        val viewModel = factory.create(BuildingsViewModel::class.java)

        // Act
        composeTestRule.setContent {
            BuildingsScreen(
                onBuildingClick = {},
                onAddBuilding = {},
                onShowTverskaya = {},
                viewModel = viewModel
            )
        }

        // Wait for UI to update
        composeTestRule.waitForIdle()

        // Assert
        composeTestRule.onNodeWithText("Тверская 10").assertExists()
        composeTestRule.onNodeWithText("Арбат 20").assertExists()
    }

    @Test
    fun addBuildingButton_isVisible() {
        // Arrange
        val factory = TestViewModelFactory()
        val viewModel = factory.create(BuildingsViewModel::class.java)

        // Act
        composeTestRule.setContent {
            BuildingsScreen(
                onBuildingClick = {},
                onAddBuilding = {},
                onShowTverskaya = {},
                viewModel = viewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("add_building_button")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun tverskayaButton_isVisible() {
        // Arrange
        val factory = TestViewModelFactory()
        val viewModel = factory.create(BuildingsViewModel::class.java)

        // Act
        composeTestRule.setContent {
            BuildingsScreen(
                onBuildingClick = {},
                onAddBuilding = {},
                onShowTverskaya = {},
                viewModel = viewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("tverskaya_button")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}