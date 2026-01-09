// app/src/main/java/com/example/buildings/data/repository/RoomBuildingRepository.kt
package com.example.buildings.data.repository

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import com.example.buildings.data.local.dao.BuildingDao
import com.example.buildings.data.mapper.BuildingMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomBuildingRepository @Inject constructor(
    private val buildingDao: BuildingDao,
    private val mapper: BuildingMapper
) : BuildingRepository {

    override fun getAllBuildings(): Flow<List<Building>> {
        return buildingDao.getAll().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun getBuildingById(id: String): Building? {
        return buildingDao.getById(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun addBuilding(building: Building) {
        buildingDao.insert(mapper.toEntity(building))
    }

    override suspend fun updateBuilding(building: Building) {
        buildingDao.update(mapper.toEntity(building))
    }

    override suspend fun deleteBuilding(building: Building) {
        buildingDao.delete(mapper.toEntity(building))
    }

    override suspend fun getBuildingsByAddress(addressQuery: String): List<Building> {
        return buildingDao.searchByAddress(addressQuery).map { mapper.toDomain(it) }
    }
}