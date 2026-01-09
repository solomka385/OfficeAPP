package com.example.buildings.presentation.building_edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import com.example.core.domain.usecase.AddBuildingUseCase
import com.example.core.domain.usecase.GetBuildingByIdUseCase
import com.example.core.domain.usecase.UpdateBuildingUseCase
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
class BuildingEditViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var repository: BuildingRepository

    private lateinit var viewModel: BuildingEditViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hiltRule.inject()

        viewModel = BuildingEditViewModel(
            getBuildingByIdUseCase = GetBuildingByIdUseCase(repository),
            addBuildingUseCase = AddBuildingUseCase(repository),
            updateBuildingUseCase = UpdateBuildingUseCase(repository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveBuilding_shouldAddNewBuildingWithoutParking() = runTest {
        // Given
        viewModel.updateAddress("Тверская 10")
        viewModel.updateBuildingType(BuildingType.WITHOUT_PARKING)

        // When
        viewModel.saveBuilding()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val buildings = repository.getAllBuildings().first()
        assertEquals(1, buildings.size)
        assertTrue(buildings[0] is Building.BuildingWithoutParking)
        assertEquals("Тверская 10", buildings[0].address)
    }

    @Test
    fun updateAddress_shouldUpdateState() {
        // When
        viewModel.updateAddress("Тверская 20")

        // Then
        assertEquals("Тверская 20", viewModel.state.value.address)
    }

    @Test
    fun loadBuilding_shouldPopulateStateForEditing() = runTest {
        // Given
        val building = Building.BuildingWithParking("1", "Тверская 10", 5, 100.0)
        repository.addBuilding(building)

        // When
        viewModel.loadBuilding("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state.isEditMode)
        assertEquals("1", state.id)
        assertEquals("Тверская 10", state.address)
        assertEquals(BuildingType.WITH_PARKING, state.buildingType)
        assertEquals("5", state.parkingSpaces)
        assertEquals("100.0", state.monthlyRent)
    }
}