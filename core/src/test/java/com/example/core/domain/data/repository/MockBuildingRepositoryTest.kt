// core/src/test/kotlin/com/example/core/data/repository/MockBuildingRepositoryTest.kt
package com.example.core.domain.data.repository

import com.example.core.data.repository.MockBuildingRepository
import com.example.core.domain.model.Building
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MockBuildingRepositoryTest {

    private lateinit var repository: MockBuildingRepository

    @Before
    fun setUp() {
        repository = MockBuildingRepository()
    }

    // ✅ КАЖДЫЙ тест, вызывающий suspend-функции, должен быть обернут в runBlocking
    @Test
    fun `getAllBuildings should return empty flow initially`() = runBlocking {
        // Act
        val result = repository.getAllBuildings().first() // first() - тоже suspend функция!

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `addBuilding should add building and update flow`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская ул., 25")

        // Act
        repository.addBuilding(building) // suspend вызов!
        val result = repository.getAllBuildings().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals(building, result[0])
    }

    @Test
    fun `addBuilding should handle multiple buildings`() = runBlocking {
        // Arrange
        val building1 = Building.BuildingWithoutParking("1", "Адрес 1")
        val building2 = Building.BuildingWithParking("2", "Адрес 2", 10, 500.0)

        // Act
        repository.addBuilding(building1) // suspend
        repository.addBuilding(building2) // suspend
        val result = repository.getAllBuildings().first() // suspend

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.contains(building1))
        assertTrue(result.contains(building2))
    }

    @Test
    fun `getBuildingById should return building when exists`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Адрес")
        repository.addBuilding(building)

        // Act
        val result = repository.getBuildingById("1") // suspend

        // Assert
        assertEquals(building, result)
    }

    @Test
    fun `getBuildingById should return null when not exists`() = runBlocking {
        // Act
        val result = repository.getBuildingById("non-existent") // suspend

        // Assert
        assertNull(result)
    }

    @Test
    fun `updateBuilding should update existing building`() = runBlocking {
        // Arrange
        val original = Building.BuildingWithoutParking("1", "Старый адрес")
        repository.addBuilding(original)

        // Act
        val updated = Building.BuildingWithoutParking("1", "Новый адрес")
        repository.updateBuilding(updated) // suspend

        val result = repository.getBuildingById("1") // suspend

        // Assert
        assertEquals("Новый адрес", result?.address)
    }

    @Test
    fun `updateBuilding should do nothing for non-existent building`() = runBlocking {
        // Arrange
        val initialCount = repository.getAllBuildings().first().size
        val nonExistent = Building.BuildingWithoutParking("999", "Адрес")

        // Act
        repository.updateBuilding(nonExistent) // suspend

        val result = repository.getBuildingById("999") // suspend
        val finalCount = repository.getAllBuildings().first().size // suspend

        // Assert
        assertNull(result)
        assertEquals(initialCount, finalCount)
    }

    @Test
    fun `deleteBuilding should remove existing building`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Адрес")
        repository.addBuilding(building)

        // Act
        repository.deleteBuilding(building) // suspend

        val result = repository.getBuildingById("1") // suspend

        // Assert
        assertNull(result)
    }

    @Test
    fun `deleteBuilding should handle multiple deletions`() = runBlocking {
        // Arrange
        val building1 = Building.BuildingWithoutParking("1", "Адрес 1")
        val building2 = Building.BuildingWithoutParking("2", "Адрес 2")
        repository.addBuilding(building1)
        repository.addBuilding(building2)

        // Act
        repository.deleteBuilding(building1)
        repository.deleteBuilding(building2)

        val result = repository.getAllBuildings().first()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getBuildingsByAddress should return matching buildings`() = runBlocking {
        // Arrange
        val building1 = Building.BuildingWithoutParking("1", "Тверская ул., 25")
        val building2 = Building.BuildingWithoutParking("2", "ул. Тверская, 10")
        val building3 = Building.BuildingWithoutParking("3", "ул. Пушкина, 5")
        repository.addBuilding(building1)
        repository.addBuilding(building2)
        repository.addBuilding(building3)

        // Act
        val result = repository.getBuildingsByAddress("Тверская") // suspend

        // Assert
        assertEquals(2, result.size)
        assertEquals(listOf("1", "2"), result.map { it.id }.sorted())
    }

    @Test
    fun `getBuildingsByAddress should be case insensitive`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "ТВЕРСКАЯ ул., 25")
        repository.addBuilding(building)

        // Act
        val resultLower = repository.getBuildingsByAddress("тверская")
        val resultUpper = repository.getBuildingsByAddress("ТВЕРСКАЯ")

        // Assert
        assertEquals(1, resultLower.size)
        assertEquals(1, resultUpper.size)
    }

    @Test
    fun `getBuildingsByAddress should return empty list for no matches`() = runBlocking {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская ул., 25")
        repository.addBuilding(building)

        // Act
        val result = repository.getBuildingsByAddress("несуществующий")

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `flow should emit on every change`() = runBlocking {
        // Arrange
        val emissions = mutableListOf<List<Building>>()

        // Запускаем сбор значений flow в отдельной корутине
        val job = launch {
            repository.getAllBuildings().collect { emissions.add(it) }
        }

        // Даем время для начальной эмиссии
        delay(100)

        // Act
        val building1 = Building.BuildingWithoutParking("1", "Адрес 1")
        repository.addBuilding(building1)
        delay(100)

        val building2 = Building.BuildingWithoutParking("2", "Адрес 2")
        repository.addBuilding(building2)
        delay(100)

        // Assert
        assertTrue(emissions.size >= 3) // Начальное + 2 добавления
        assertEquals(0, emissions[0].size)
        assertEquals(1, emissions[1].size)
        assertEquals(2, emissions[2].size)

        job.cancel()
    }

    @Test
    fun `repository should handle mixed building types`() = runBlocking {
        // Arrange
        val building1 = Building.BuildingWithoutParking("1", "Адрес 1")
        val building2 = Building.BuildingWithParking("2", "Адрес 2", 10, 500.0)

        // Act
        repository.addBuilding(building1)
        repository.addBuilding(building2)

        val allBuildings = repository.getAllBuildings().first()
        val buildingWithParking = repository.getBuildingById("2") as Building.BuildingWithParking

        // Assert
        assertEquals(2, allBuildings.size)
        assertEquals(10, buildingWithParking.parkingSpaces)
        assertEquals(500.0, buildingWithParking.monthlyParkingRent, 0.001)
    }
}