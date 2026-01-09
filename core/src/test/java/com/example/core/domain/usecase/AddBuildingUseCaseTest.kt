package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AddBuildingUseCaseTest {

    private lateinit var repository: BuildingRepository
    private lateinit var useCase: AddBuildingUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = AddBuildingUseCase(repository)
    }

    @Test
    fun `invoke should add building to repository`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская ул., 25")
        coEvery { repository.addBuilding(building) } returns Unit

        // Act
        useCase(building)

        // Assert
        coVerify { repository.addBuilding(building) }
    }

    @Test
    fun `invoke should add building with parking`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithParking("1", "Адрес", 10, 500.0)
        coEvery { repository.addBuilding(building) } returns Unit

        // Act
        useCase(building)

        // Assert
        coVerify { repository.addBuilding(building) }
    }
}