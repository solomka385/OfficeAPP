// core/src/main/kotlin/com/example/core/domain/usecase/GetAllBuildingsUseCase.kt
package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository
import kotlinx.coroutines.flow.Flow

class GetAllBuildingsUseCase(
    private val repository: BuildingRepository
) {
    operator fun invoke(): Flow<List<Building>> = repository.getAllBuildings()
}