// core/src/main/kotlin/com/example/core/domain/usecase/GetBuildingByIdUseCase.kt
package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository

class GetBuildingByIdUseCase(
    private val repository: BuildingRepository
) {
    suspend operator fun invoke(id: String): Building? = repository.getBuildingById(id)
}