package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class UpdateBuildingUseCaseTest {

    private lateinit var repository: BuildingRepository
    private lateinit var useCase: UpdateBuildingUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = UpdateBuildingUseCase(repository)
    }

    @Test
    fun `invoke should update building in repository`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Новый адрес")
        coEvery { repository.updateBuilding(building) } returns Unit

        // Act
        useCase(building)

        // Assert
        coVerify { repository.updateBuilding(building) }
    }

    @Test
    fun `invoke should update building with parking`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithParking("1", "Новый адрес", 20, 600.0)
        coEvery { repository.updateBuilding(building) } returns Unit

        // Act
        useCase(building)

        // Assert
        coVerify { repository.updateBuilding(building) }
    }
}