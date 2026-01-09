package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetBuildingsByAddressUseCaseTest {

    private lateinit var repository: BuildingRepository
    private lateinit var useCase: GetBuildingsByAddressUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetBuildingsByAddressUseCase(repository)
    }

    @Test
    fun `invoke should return matching buildings`() = runBlocking {
        // Arrange
        val buildings = listOf(
            Building.BuildingWithoutParking("1", "Тверская ул., 25"),
            Building.BuildingWithoutParking("2", "ул. Тверская, 10")
        )
        coEvery { repository.getBuildingsByAddress("Тверская") } returns buildings

        // Act
        val result = useCase("Тверская")

        // Assert
        assertEquals(2, result.size)
        assertEquals(listOf("1", "2"), result.map { it.id })
    }

    @Test
    fun `invoke should be case insensitive via repository`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "ТВЕРСКАЯ ул., 25")
        coEvery { repository.getBuildingsByAddress("тверская") } returns listOf(building)
        coEvery { repository.getBuildingsByAddress("ТВЕРСКАЯ") } returns listOf(building)

        // Act
        val resultLower = useCase("тверская")
        val resultUpper = useCase("ТВЕРСКАЯ")

        // Assert
        assertEquals(1, resultLower.size)
        assertEquals(1, resultUpper.size)
    }

    @Test
    fun `invoke should return empty list for no matches`() = runBlocking {
        // Arrange
        coEvery { repository.getBuildingsByAddress("несуществующий адрес") } returns emptyList()

        // Act
        val result = useCase("несуществующий адрес")

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke should handle partial matches`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "123 Тверская улица, дом 45")
        coEvery { repository.getBuildingsByAddress("Тверская") } returns listOf(building)
        coEvery { repository.getBuildingsByAddress("улица") } returns listOf(building)
        coEvery { repository.getBuildingsByAddress("45") } returns listOf(building)

        // Act
        val result1 = useCase("Тверская")
        val result2 = useCase("улица")
        val result3 = useCase("45")

        // Assert
        assertEquals(1, result1.size)
        assertEquals(1, result2.size)
        assertEquals(1, result3.size)
    }
}