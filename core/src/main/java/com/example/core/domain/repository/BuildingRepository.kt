// core/src/main/kotlin/com/example/core/domain/repository/BuildingRepository.kt
package com.example.core.domain.repository

import com.example.core.domain.model.Building
import kotlinx.coroutines.flow.Flow

interface BuildingRepository {
    fun getAllBuildings(): Flow<List<Building>>
    suspend fun getBuildingById(id: String): Building?
    suspend fun addBuilding(building: Building)
    suspend fun updateBuilding(building: Building)
    suspend fun deleteBuilding(building: Building)
    suspend fun getBuildingsByAddress(addressQuery: String): List<Building>
}