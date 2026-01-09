package com.example.buildings.presentation.building_detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import com.example.core.domain.usecase.DeleteBuildingUseCase
import com.example.core.domain.usecase.GetBuildingByIdUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class BuildingDetailViewModelTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var repository: BuildingRepository

    private lateinit var viewModel: BuildingDetailViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hiltRule.inject()

        viewModel = BuildingDetailViewModel(
            getBuildingByIdUseCase = GetBuildingByIdUseCase(repository),
            deleteBuildingUseCase = DeleteBuildingUseCase(repository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadBuilding_shouldUpdateStateToSuccess_whenBuildingExists() = runTest {
        // Given
        val building = Building.BuildingWithoutParking("1", "Тверская 10")
        repository.addBuilding(building)

        // When
        viewModel.loadBuilding("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state is BuildingDetailState.Success)
        val successState = state as BuildingDetailState.Success
        assertEquals(building, successState.building)
    }

    @Test
    fun loadBuilding_shouldUpdateStateToError_whenBuildingNotFound() = runTest {
        // When
        viewModel.loadBuilding("999")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertTrue(state is BuildingDetailState.Error)
    }

    @Test
    fun deleteBuilding_shouldRemoveBuildingFromRepository() = runTest {
        // Given
        val building = Building.BuildingWithoutParking("1", "Тверская 10")
        repository.addBuilding(building)
        viewModel.loadBuilding("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.deleteBuilding()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val deletedBuilding = repository.getBuildingById("1")
        assertNull(deletedBuilding)
    }
}