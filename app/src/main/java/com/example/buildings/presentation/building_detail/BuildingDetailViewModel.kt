// app/src/main/java/com/example/buildings/presentation/building_detail/BuildingDetailViewModel.kt
package com.example.buildings.presentation.building_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Building
import com.example.core.domain.usecase.DeleteBuildingUseCase
import com.example.core.domain.usecase.GetBuildingByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuildingDetailViewModel @Inject constructor(
    private val getBuildingByIdUseCase: GetBuildingByIdUseCase,
    private val deleteBuildingUseCase: DeleteBuildingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<BuildingDetailState>(BuildingDetailState.Loading)
    val state: StateFlow<BuildingDetailState> = _state

    fun loadBuilding(buildingId: String) {
        viewModelScope.launch {
            _state.value = BuildingDetailState.Loading
            try {
                val building = getBuildingByIdUseCase(buildingId)
                if (building != null) {
                    _state.value = BuildingDetailState.Success(building)
                } else {
                    _state.value = BuildingDetailState.Error("Building not found")
                }
            } catch (e: Exception) {
                _state.value = BuildingDetailState.Error("Failed to load building: ${e.message}")
            }
        }
    }

    fun deleteBuilding() {
        viewModelScope.launch {
            val currentBuilding = (state.value as? BuildingDetailState.Success)?.building
            if (currentBuilding != null) {
                try {
                    deleteBuildingUseCase(currentBuilding)
                } catch (_: Exception) {
                    // Handle error
                }
            }
        }
    }
}

sealed class BuildingDetailState {
    object Loading : BuildingDetailState()
    data class Success(val building: Building) : BuildingDetailState()
    data class Error(val message: String) : BuildingDetailState()
}