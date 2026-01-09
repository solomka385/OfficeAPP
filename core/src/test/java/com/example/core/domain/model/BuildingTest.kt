package com.example.core.domain.model

import org.junit.Assert.*
import org.junit.Test

class BuildingTest {

    @Test
    fun `BuildingWithoutParking should correctly store properties`() {
        // Arrange
        val id = "test-id-123"
        val address = "Тверская ул., 25"

        // Act
        val building = Building.BuildingWithoutParking(id, address)

        // Assert
        assertEquals(id, building.id)
        assertEquals(address, building.address)
    }

    @Test
    fun `BuildingWithParking should correctly store properties`() {
        // Arrange
        val id = "test-id-456"
        val address = "ул. Пушкина, 10"
        val parkingSpaces = 50
        val monthlyRent = 3000.0

        // Act
        val building = Building.BuildingWithParking(id, address, parkingSpaces, monthlyRent)

        // Assert
        assertEquals(id, building.id)
        assertEquals(address, building.address)
        assertEquals(parkingSpaces, building.parkingSpaces)
        assertEquals(monthlyRent, building.monthlyParkingRent, 0.001)
    }

    @Test
    fun `Data classes should have correct equals and hashCode`() {
        // Arrange
        val building1 = Building.BuildingWithoutParking("1", "Адрес 1")
        val building2 = Building.BuildingWithoutParking("1", "Адрес 1")
        val building3 = Building.BuildingWithoutParking("2", "Адрес 2")

        // Assert
        assertEquals(building1, building2)  // Same properties
        assertNotEquals(building1, building3) // Different id
        assertEquals(building1.hashCode(), building2.hashCode())
    }

    @Test
    fun `Copy method should create modified copy`() {
        // Arrange
        val original = Building.BuildingWithParking("1", "Старый адрес", 10, 500.0)

        // Act
        val copied = original.copy(address = "Новый адрес", parkingSpaces = 20)

        // Assert
        assertEquals("1", copied.id)
        assertEquals("Новый адрес", copied.address)
        assertEquals(20, copied.parkingSpaces)
        assertEquals(500.0, copied.monthlyParkingRent, 0.001)
        assertNotSame(original, copied)
    }

    @Test
    fun `ToString should provide meaningful representation`() {
        // Arrange
        val building = Building.BuildingWithoutParking("test-id", "Test Address")

        // Act
        val stringRepresentation = building.toString()

        // Assert
        assertTrue(stringRepresentation.contains("test-id"))
        assertTrue(stringRepresentation.contains("Test Address"))
    }
}