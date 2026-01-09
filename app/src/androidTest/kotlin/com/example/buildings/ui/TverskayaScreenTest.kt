package com.example.buildings.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buildings.presentation.buildings.BuildingsViewModel
import com.example.buildings.presentation.tverskaya.TverskayaScreen
import com.example.buildings.utils.TestViewModelFactory
import com.example.core.domain.model.Building
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TverskayaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun title_isDisplayed() {
        // Arrange
        val factory = TestViewModelFactory()
        val building1 = Building.BuildingWithoutParking("1", "Тверская 1")
        val building2 = Building.BuildingWithoutParking("2", "Арбат 2")
        factory.addTestBuildings(building1, building2)
        val viewModel = factory.create(BuildingsViewModel::class.java)

        // Act
        composeTestRule.setContent {
            TverskayaScreen(
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Здания на Тверской").assertIsDisplayed()
    }

    @Test
    fun backButton_isDisplayed() {
        // Arrange
        val factory = TestViewModelFactory()
        val viewModel = factory.create(BuildingsViewModel::class.java)

        // Act
        composeTestRule.setContent {
            TverskayaScreen(
                onNavigateBack = {},
                viewModel = viewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithContentDescription("Назад").assertIsDisplayed()
    }
}