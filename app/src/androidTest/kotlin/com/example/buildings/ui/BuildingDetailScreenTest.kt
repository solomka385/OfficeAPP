package com.example.buildings.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buildings.presentation.building_detail.BuildingDetailScreen
import com.example.buildings.utils.TestViewModelFactory
import com.example.core.domain.model.Building
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BuildingDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenBuildingWithParking_showsDetails() {
        // Arrange
        val factory = TestViewModelFactory()
        val building = Building.BuildingWithParking("1", "Тверская 10", 5, 100.0)
        factory.addTestBuildings(building)

        val viewModel = factory.create(com.example.buildings.presentation.building_detail.BuildingDetailViewModel::class.java)

        // Загружаем здание в ViewModel
        runBlocking {
            viewModel.loadBuilding("1")
        }

        // Act
        composeTestRule.setContent {
            BuildingDetailScreen(
                buildingId = "1",
                onEditBuilding = {},
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        // Wait for UI
        composeTestRule.waitForIdle()

        // Assert
        composeTestRule.onNodeWithTag("address_text").assertIsDisplayed()
        composeTestRule.onNodeWithText("Тверская 10").assertIsDisplayed()
        composeTestRule.onNodeWithTag("parking_spaces_text").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
        composeTestRule.onNodeWithTag("rent_text").assertIsDisplayed()
    }

    @Test
    fun whenLoading_showsProgress() {
        // Arrange
        val factory = TestViewModelFactory()
        val viewModel = factory.create(com.example.buildings.presentation.building_detail.BuildingDetailViewModel::class.java)

        // Act
        composeTestRule.setContent {
            BuildingDetailScreen(
                buildingId = "1",
                onEditBuilding = {},
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        // Wait for UI
        composeTestRule.waitForIdle()

        // Assert
        composeTestRule.onNodeWithTag("loading_indicator").assertExists()
    }

    @Test
    fun editButton_isDisplayed() {
        // Arrange
        val factory = TestViewModelFactory()
        val building = Building.BuildingWithoutParking("1", "Тверская 10")
        factory.addTestBuildings(building)

        val viewModel = factory.create(com.example.buildings.presentation.building_detail.BuildingDetailViewModel::class.java)

        // Загружаем здание
        runBlocking {
            viewModel.loadBuilding("1")
        }

        // Act
        composeTestRule.setContent {
            BuildingDetailScreen(
                buildingId = "1",
                onEditBuilding = {},
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        // Wait for UI
        composeTestRule.waitForIdle()

        // Assert
        composeTestRule.onNodeWithTag("edit_button").assertIsDisplayed()
    }
}