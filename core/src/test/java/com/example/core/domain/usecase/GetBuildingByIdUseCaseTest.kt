package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetBuildingByIdUseCaseTest {

    private lateinit var repository: BuildingRepository
    private lateinit var useCase: GetBuildingByIdUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetBuildingByIdUseCase(repository)
    }

    @Test
    fun `invoke should return building when id exists`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская ул., 25")
        coEvery { repository.getBuildingById("1") } returns building

        // Act
        val result = useCase("1")

        // Assert
        assertEquals(building, result)
    }

    @Test
    fun `invoke should return null when id does not exist`() = runBlocking {
        // Arrange
        coEvery { repository.getBuildingById("1") } returns null

        // Act
        val result = useCase("1")

        // Assert
        assertNull(result)
    }

    @Test
    fun `invoke should handle building with parking`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithParking("1", "Адрес", 10, 500.0)
        coEvery { repository.getBuildingById("1") } returns building

        // Act
        val result = useCase("1")

        // Assert
        assertTrue(result is Building.BuildingWithParking)
        assertEquals(10, (result as Building.BuildingWithParking).parkingSpaces)
    }
}