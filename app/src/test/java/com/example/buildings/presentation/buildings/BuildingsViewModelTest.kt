package com.example.buildings.presentation.buildings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import com.example.core.domain.usecase.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class BuildingsViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var repository: BuildingRepository

    private lateinit var viewModel: BuildingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hiltRule.inject()

        viewModel = BuildingsViewModel(
            getAllBuildingsUseCase = GetAllBuildingsUseCase(repository),
            deleteBuildingUseCase = DeleteBuildingUseCase(repository),
            getBuildingsByAddressUseCase = GetBuildingsByAddressUseCase(repository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getTverskayaBuildings_returnsFilteredList() = runTest {
        // Arrange
        repository.addBuilding(Building.BuildingWithoutParking("1", "Тверская 10"))
        repository.addBuilding(Building.BuildingWithoutParking("2", "Арбат 20"))
        repository.addBuilding(Building.BuildingWithoutParking("3", "Тверская 30"))

        // Act
        val result = viewModel.getTverskayaBuildings()

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.all { it.address.contains("Тверская") })
    }

    @Test
    fun initialState_isEmpty() = runTest {
        // Act
        val result = viewModel.state.first()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun deleteBuilding_removesFromList() = runTest {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская 10")
        repository.addBuilding(building)

        // Act
        viewModel.deleteBuilding(building)

        // Даем время для обновления состояния
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val result = viewModel.state.first()
        assertFalse(result.contains(building))
    }
}