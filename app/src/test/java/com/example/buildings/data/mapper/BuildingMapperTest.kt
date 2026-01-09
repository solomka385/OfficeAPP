// app/src/test/java/com/example/buildings/data/mapper/BuildingMapperTest.kt
package com.example.buildings.data.mapper

import com.example.buildings.data.local.entity.BuildingEntity
import com.example.core.domain.model.Building
import org.junit.Assert.*
import org.junit.Test

class BuildingMapperTest {

    private val mapper = BuildingMapper()

    @Test
    fun `toDomain should map BuildingEntity to BuildingWithoutParking`() {
        // Given
        val entity = BuildingEntity(
            id = "1",
            address = "Тверская 10",
            type = "WITHOUT_PARKING",
            parkingSpaces = null,
            monthlyParkingRent = null
        )

        // When
        val result = mapper.toDomain(entity)

        // Then
        assertTrue(result is Building.BuildingWithoutParking)
        assertEquals("1", result.id)
        assertEquals("Тверская 10", result.address)
    }

    @Test
    fun `toDomain should map BuildingEntity to BuildingWithParking`() {
        // Given
        val entity = BuildingEntity(
            id = "2",
            address = "Тверская 20",
            type = "WITH_PARKING",
            parkingSpaces = 5,
            monthlyParkingRent = 100.0
        )

        // When
        val result = mapper.toDomain(entity)

        // Then
        assertTrue(result is Building.BuildingWithParking)
        val building = result as Building.BuildingWithParking
        assertEquals("2", building.id)
        assertEquals("Тверская 20", building.address)
        assertEquals(5, building.parkingSpaces)
        assertEquals(100.0, building.monthlyParkingRent, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `toDomain should throw exception for unknown type`() {
        // Given
        val entity = BuildingEntity(
            id = "3",
            address = "Test",
            type = "UNKNOWN_TYPE",
            parkingSpaces = null,
            monthlyParkingRent = null
        )

        // When
        mapper.toDomain(entity)
    }

    @Test
    fun `toEntity should map BuildingWithoutParking to BuildingEntity`() {
        // Given
        val building = Building.BuildingWithoutParking(
            id = "1",
            address = "Тверская 10"
        )

        // When
        val result = mapper.toEntity(building)

        // Then
        assertEquals("1", result.id)
        assertEquals("Тверская 10", result.address)
        assertEquals("WITHOUT_PARKING", result.type)
        assertNull(result.parkingSpaces)
        assertNull(result.monthlyParkingRent)
    }

    @Test
    fun `toEntity should map BuildingWithParking to BuildingEntity`() {
        // Given
        val building = Building.BuildingWithParking(
            id = "2",
            address = "Тверская 20",
            parkingSpaces = 5,
            monthlyParkingRent = 100.0
        )

        // When
        val result = mapper.toEntity(building)

        // Then
        assertEquals("2", result.id)
        assertEquals("Тверская 20", result.address)
        assertEquals("WITH_PARKING", result.type)
        assertEquals(5, result.parkingSpaces)
        result.monthlyParkingRent?.let { assertEquals(100.0, it, 0.001) }
    }
}