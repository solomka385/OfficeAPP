package com.example.buildings.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.buildings.presentation.building_edit.BuildingEditScreen
import com.example.buildings.presentation.building_edit.BuildingEditViewModel
import com.example.buildings.utils.TestViewModelFactory
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BuildingEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addressField_isVisible() {
        // Arrange
        val factory = TestViewModelFactory()
        val viewModel = factory.create(BuildingEditViewModel::class.java)

        // Act
        composeTestRule.setContent {
            BuildingEditScreen(
                buildingId = null,
                onSaveComplete = {},
                onCancel = {},
                viewModel = viewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("address_field").assertIsDisplayed()
    }

    @Test
    fun buildingTypeSelector_isVisible() {
        // Arrange
        val factory = TestViewModelFactory()
        val viewModel = factory.create(BuildingEditViewModel::class.java)

        // Act
        composeTestRule.setContent {
            BuildingEditScreen(
                buildingId = null,
                onSaveComplete = {},
                onCancel = {},
                viewModel = viewModel
            )
        }

        // Assert
        composeTestRule.onNodeWithTag("building_type_selector").assertIsDisplayed()
    }
}