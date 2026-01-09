package com.example.buildings.presentation.building_detail

import com.example.core.domain.model.Building
import kotlinx.coroutines.flow.StateFlow

interface BuildingDetailViewModelInterface {
    val state: StateFlow<BuildingDetailState>
    fun loadBuilding(buildingId: String)
    fun deleteBuilding()
}

sealed interface BuildingDetailState {
    object Loading : BuildingDetailState
    data class Success(val building: Building) : BuildingDetailState
    object Error : BuildingDetailState
}