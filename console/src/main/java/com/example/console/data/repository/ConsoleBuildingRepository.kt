// console/src/main/kotlin/com/example/buildings/console/data/repository/ConsoleBuildingRepository.kt
package com.example.buildings.console.data.repository

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ConsoleBuildingRepository : BuildingRepository {
    private val buildings = mutableListOf<Building>()
    private val buildingsFlow = MutableStateFlow<List<Building>>(emptyList())

    override fun getAllBuildings(): Flow<List<Building>> = buildingsFlow

    override suspend fun getBuildingById(id: String): Building? {
        return buildings.find { it.id == id }
    }

    override suspend fun addBuilding(building: Building) {
        buildings.add(building)
        buildingsFlow.value = buildings.toList()
    }

    override suspend fun updateBuilding(building: Building) {
        val index = buildings.indexOfFirst { it.id == building.id }
        if (index != -1) {
            buildings[index] = building
            buildingsFlow.value = buildings.toList()
        }
    }

    override suspend fun deleteBuilding(building: Building) {
        buildings.removeAll { it.id == building.id }
        buildingsFlow.value = buildings.toList()
    }

    override suspend fun getBuildingsByAddress(addressQuery: String): List<Building> {
        return buildings.filter {
            it.address.contains(addressQuery, ignoreCase = true)
        }
    }
}
