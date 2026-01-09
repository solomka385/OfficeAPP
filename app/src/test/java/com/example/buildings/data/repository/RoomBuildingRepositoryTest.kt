// app/src/test/java/com/example/buildings/data/repository/RoomBuildingRepositoryTest.kt
package com.example.buildings.data.repository

import com.example.buildings.data.repository.FakeBuildingRepository
import com.example.core.domain.model.Building
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RoomBuildingRepositoryTest {

    private lateinit var repository: FakeBuildingRepository

    @Before
    fun setUp() {
        repository = FakeBuildingRepository()
    }

    @Test
    fun `getAllBuildings should return all buildings`() = runTest {
        // Arrange
        val building1 = Building.BuildingWithoutParking("1", "Тверская 1")
        val building2 = Building.BuildingWithParking("2", "Тверская 2", 5, 100.0)
        repository.addTestBuildings(building1, building2)

        // Act
        val result = repository.getAllBuildings().first()

        // Assert
        assertEquals("Should have 2 buildings", 2, result.size)
        assertTrue("Should contain building1", result.contains(building1))
        assertTrue("Should contain building2", result.contains(building2))
    }

    @Test
    fun `getBuildingById should return correct building`() = runTest {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская 1")
        repository.addTestBuildings(building)

        // Act
        val result = repository.getBuildingById("1")

        // Assert
        assertNotNull("Building should exist", result)
        assertEquals("ID should match", "1", result?.id)
        assertEquals("Address should match", "Тверская 1", result?.address)
    }

    @Test
    fun `getBuildingById should return null for non-existent building`() = runTest {
        // Act
        val result = repository.getBuildingById("999")

        // Assert
        assertNull("Should return null", result)
    }

    @Test
    fun `addBuilding should add new building`() = runTest {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская 1")

        // Act
        repository.addBuilding(building)
        val result = repository.getAllBuildings().first()

        // Assert
        assertEquals("Should have 1 building", 1, result.size)
        assertEquals("Building should match", building, result[0])
    }

    @Test
    fun `updateBuilding should update existing building`() = runTest {
        // Arrange
        val original = Building.BuildingWithoutParking("1", "Тверская 1")
        val updated = Building.BuildingWithoutParking("1", "Тверская 1 обновленный")
        repository.addTestBuildings(original)

        // Act
        repository.updateBuilding(updated)
        val result = repository.getBuildingById("1")

        // Assert
        assertEquals("Address should be updated", "Тверская 1 обновленный", result?.address)
    }

    @Test
    fun `deleteBuilding should remove building`() = runTest {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "Тверская 1")
        repository.addTestBuildings(building)

        // Act
        repository.deleteBuilding(building)
        val result = repository.getAllBuildings().first()

        // Assert
        assertEquals("Should have 0 buildings", 0, result.size)
    }

    @Test
    fun `getBuildingsByAddress should filter by Tverskaya`() = runTest {
        // Arrange
        val tverskaya1 = Building.BuildingWithoutParking("1", "Тверская 1")
        val arbat = Building.BuildingWithoutParking("2", "Арбат 2")
        val tverskaya2 = Building.BuildingWithoutParking("3", "Тверская 3")
        repository.addTestBuildings(tverskaya1, arbat, tverskaya2)

        // Act
        val result = repository.getBuildingsByAddress("Тверская")

        // Assert
        assertEquals("Should have 2 Tverskaya buildings", 2, result.size)
        assertTrue("All should contain Тверская",
            result.all { it.address.contains("Тверская") })
    }

    @Test
    fun `getBuildingsByAddress should be case insensitive`() = runTest {
        // Arrange
        val building = Building.BuildingWithoutParking("1", "тВерСкая 1")
        repository.addTestBuildings(building)

        // Act
        val result = repository.getBuildingsByAddress("Тверская")

        // Assert
        assertEquals("Should find building with different case", 1, result.size)
    }
}