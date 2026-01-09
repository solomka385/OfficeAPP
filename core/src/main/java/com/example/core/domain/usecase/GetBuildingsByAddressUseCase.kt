// core/src/main/kotlin/com/example/core/domain/usecase/GetBuildingsByAddressUseCase.kt
package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import com.example.core.domain.repository.BuildingRepository

class GetBuildingsByAddressUseCase(
    private val repository: BuildingRepository
) {
    suspend operator fun invoke(addressQuery: String): List<Building> {
        return repository.getBuildingsByAddress(addressQuery)
    }
}