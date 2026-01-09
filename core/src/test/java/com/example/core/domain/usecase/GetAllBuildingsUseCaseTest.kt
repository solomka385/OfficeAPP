package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetAllBuildingsUseCaseTest {

    private lateinit var repository: BuildingRepository
    private lateinit var useCase: GetAllBuildingsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetAllBuildingsUseCase(repository)
    }

    @Test
    fun `invoke should return flow from repository`() = runBlocking {
        // Arrange
        val buildings = listOf(
            Building.BuildingWithoutParking("1", "Тверская ул., 25"),
            Building.BuildingWithParking("2", "ул. Пушкина, 10", 50, 3000.0)
        )
        coEvery { repository.getAllBuildings() } returns flowOf(buildings)

        // Act
        val result = useCase().first()

        // Assert
        assertEquals(2, result.size)
        assertEquals("1", result[0].id)
        assertEquals("2", result[1].id)
    }

    @Test
    fun `invoke should return empty flow when repository is empty`() = runBlocking {
        // Arrange
        coEvery { repository.getAllBuildings() } returns flowOf(emptyList())

        // Act
        val result = useCase().first()

        // Assert
        assertTrue(result.isEmpty())
    }
}