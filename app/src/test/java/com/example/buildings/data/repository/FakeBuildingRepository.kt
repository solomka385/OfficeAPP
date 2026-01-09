package com.example.buildings.data.repository

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeBuildingRepository : BuildingRepository {
    private val _buildings = mutableListOf<Building>()
    private val _buildingsFlow = MutableStateFlow(_buildings.toList())

    override fun getAllBuildings(): StateFlow<List<Building>> {
        return _buildingsFlow
    }

    override suspend fun getBuildingById(id: String): Building? {
        return _buildings.find { it.id == id }
    }

    override suspend fun addBuilding(building: Building) {
        _buildings.add(building)
        _buildingsFlow.value = _buildings.toList()
    }

    override suspend fun updateBuilding(building: Building) {
        val index = _buildings.indexOfFirst { it.id == building.id }
        if (index != -1) {
            _buildings[index] = building
            _buildingsFlow.value = _buildings.toList()
        }
    }

    override suspend fun deleteBuilding(building: Building) {
        _buildings.removeAll { it.id == building.id }
        _buildingsFlow.value = _buildings.toList()
    }

    override suspend fun getBuildingsByAddress(addressQuery: String): List<Building> {
        return _buildings.filter {
            it.address.contains(addressQuery, ignoreCase = true)
        }
    }

    // Методы для тестов
    fun clear() {
        _buildings.clear()
        _buildingsFlow.value = emptyList()
    }

    fun addTestBuildings(vararg buildings: Building) {
        _buildings.addAll(buildings)
        _buildingsFlow.value = _buildings.toList()
    }
}