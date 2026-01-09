package com.example.buildings.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.buildings.presentation.buildings.BuildingsViewModel
import com.example.buildings.presentation.building_detail.BuildingDetailViewModel
import com.example.buildings.presentation.building_edit.BuildingEditViewModel
import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import com.example.core.domain.usecase.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Фейковый репозиторий для UI-тестов
class FakeTestRepository : BuildingRepository {
    private val _buildings = mutableListOf<Building>()
    private val _buildingsFlow = MutableStateFlow<List<Building>>(_buildings)

    override fun getAllBuildings(): StateFlow<List<Building>> = _buildingsFlow

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

    // Вспомогательные методы для тестов
    fun clear() {
        _buildings.clear()
        _buildingsFlow.value = emptyList()
    }

    fun addTestBuildings(vararg buildings: Building) {
        _buildings.addAll(buildings)
        _buildingsFlow.value = _buildings.toList()
    }
}

// Фабрика для создания ViewModel с тестовыми зависимостями
class TestViewModelFactory : ViewModelProvider.Factory {

    private val repository = FakeTestRepository()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BuildingsViewModel::class.java) -> {
                BuildingsViewModel(
                    getAllBuildingsUseCase = GetAllBuildingsUseCase(repository),
                    deleteBuildingUseCase = DeleteBuildingUseCase(repository),
                    getBuildingsByAddressUseCase = GetBuildingsByAddressUseCase(repository)
                ) as T
            }
            modelClass.isAssignableFrom(BuildingDetailViewModel::class.java) -> {
                BuildingDetailViewModel(
                    getBuildingByIdUseCase = GetBuildingByIdUseCase(repository),
                    deleteBuildingUseCase = DeleteBuildingUseCase(repository)
                ) as T
            }
            modelClass.isAssignableFrom(BuildingEditViewModel::class.java) -> {
                BuildingEditViewModel(
                    getBuildingByIdUseCase = GetBuildingByIdUseCase(repository),
                    addBuildingUseCase = AddBuildingUseCase(repository),
                    updateBuildingUseCase = UpdateBuildingUseCase(repository)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    // Методы для настройки тестовых данных
    fun addTestBuildings(vararg buildings: Building) {
        repository.addTestBuildings(*buildings)
    }

    fun clear() {
        repository.clear()
    }
}