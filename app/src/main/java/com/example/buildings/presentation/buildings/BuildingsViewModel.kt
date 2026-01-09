package com.example.buildings.presentation.buildings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Building
import com.example.core.domain.usecase.DeleteBuildingUseCase
import com.example.core.domain.usecase.GetAllBuildingsUseCase
import com.example.core.domain.usecase.GetBuildingsByAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuildingsViewModel @Inject constructor(
    getAllBuildingsUseCase: GetAllBuildingsUseCase,
    private val deleteBuildingUseCase: DeleteBuildingUseCase,
    private val getBuildingsByAddressUseCase: GetBuildingsByAddressUseCase
) : ViewModel() {

    val state: StateFlow<List<Building>> = getAllBuildingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun deleteBuilding(building: Building) {
        viewModelScope.launch {
            deleteBuildingUseCase(building)
        }
    }

    suspend fun getTverskayaBuildings(): List<Building> {
        return getBuildingsByAddressUseCase("Тверская")
    }
}