// app/src/main/java/com/example/buildings/data/mapper/BuildingMapper.kt
package com.example.buildings.data.mapper

import com.example.core.domain.model.Building
import com.example.buildings.data.local.entity.BuildingEntity
import javax.inject.Inject

class BuildingMapper @Inject constructor() {
    fun toDomain(entity: BuildingEntity): Building {
        return when (entity.type) {
            "WITHOUT_PARKING" -> Building.BuildingWithoutParking(
                id = entity.id,
                address = entity.address
            )
            "WITH_PARKING" -> Building.BuildingWithParking(
                id = entity.id,
                address = entity.address,
                parkingSpaces = entity.parkingSpaces ?: 0,
                monthlyParkingRent = entity.monthlyParkingRent ?: 0.0
            )
            else -> throw IllegalArgumentException("Unknown building type: ${entity.type}")
        }
    }

    fun toEntity(building: Building): BuildingEntity {
        return when (building) {
            is Building.BuildingWithoutParking -> BuildingEntity(
                id = building.id,
                address = building.address,
                type = "WITHOUT_PARKING",
                parkingSpaces = null,
                monthlyParkingRent = null
            )
            is Building.BuildingWithParking -> BuildingEntity(
                id = building.id,
                address = building.address,
                type = "WITH_PARKING",
                parkingSpaces = building.parkingSpaces,
                monthlyParkingRent = building.monthlyParkingRent
            )
        }
    }
}