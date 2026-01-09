// core/src/main/kotlin/com/example/core/domain/usecase/UpdateBuildingUseCase.kt
package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository

class UpdateBuildingUseCase(
    private val repository: BuildingRepository
) {
    suspend operator fun invoke(building: Building) = repository.updateBuilding(building)
}