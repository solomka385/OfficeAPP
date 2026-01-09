// app/src/main/java/com/example/buildings/presentation/building_edit/BuildingEditViewModel.kt
package com.example.buildings.presentation.building_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Building
import com.example.core.domain.usecase.AddBuildingUseCase
import com.example.core.domain.usecase.GetBuildingByIdUseCase
import com.example.core.domain.usecase.UpdateBuildingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class BuildingEditViewModel @Inject constructor(
    private val getBuildingByIdUseCase: GetBuildingByIdUseCase,
    private val addBuildingUseCase: AddBuildingUseCase,
    private val updateBuildingUseCase: UpdateBuildingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BuildingEditState())
    val state: StateFlow<BuildingEditState> = _state

    private val _uiEvent = MutableStateFlow<BuildingEditUiEvent?>(null)
    val uiEvent: StateFlow<BuildingEditUiEvent?> = _uiEvent

    fun loadBuilding(buildingId: String?) {
        if (buildingId == null) {
            _state.value = BuildingEditState(
                buildingType = BuildingType.WITHOUT_PARKING,
                isEditMode = false
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val building = getBuildingByIdUseCase(buildingId)
                if (building != null) {
                    _state.value = BuildingEditState(
                        id = building.id,
                        address = building.address,
                        buildingType = when (building) {
                            is Building.BuildingWithoutParking -> BuildingType.WITHOUT_PARKING
                            is Building.BuildingWithParking -> BuildingType.WITH_PARKING
                        },
                        parkingSpaces = (building as? Building.BuildingWithParking)?.parkingSpaces?.toString() ?: "",
                        monthlyRent = (building as? Building.BuildingWithParking)?.monthlyParkingRent?.toString() ?: "",
                        isEditMode = true,
                        isLoading = false
                    )
                } else {
                    _uiEvent.value = BuildingEditUiEvent.ShowError("building_not_found")
                }
            } catch (e: Exception) {
                _uiEvent.value = BuildingEditUiEvent.ShowError("load_building_error")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun updateAddress(address: String) {
        _state.value = _state.value.copy(
            address = address,
            addressError = if (address.isBlank()) ValidationError.ADDRESS_EMPTY.key else null
        )
    }

    fun updateBuildingType(buildingType: BuildingType) {
        _state.value = _state.value.copy(
            buildingType = buildingType,
            parkingSpacesError = if (buildingType == BuildingType.WITH_PARKING && _state.value.parkingSpaces.isBlank()) {
                ValidationError.PARKING_SPACES_REQUIRED.key
            } else null,
            monthlyRentError = if (buildingType == BuildingType.WITH_PARKING && _state.value.monthlyRent.isBlank()) {
                ValidationError.MONTHLY_RENT_REQUIRED.key
            } else null
        )
    }

    fun updateParkingSpaces(parkingSpaces: String) {
        val error = when {
            _state.value.buildingType == BuildingType.WITH_PARKING && parkingSpaces.isBlank() ->
                ValidationError.PARKING_SPACES_REQUIRED.key
            parkingSpaces.toIntOrNull() == null -> ValidationError.PARKING_SPACES_INVALID.key
            parkingSpaces.toInt() <= 0 -> ValidationError.PARKING_SPACES_POSITIVE.key
            else -> null
        }

        _state.value = _state.value.copy(
            parkingSpaces = parkingSpaces,
            parkingSpacesError = error
        )
    }

    fun updateMonthlyRent(monthlyRent: String) {
        val error = when {
            _state.value.buildingType == BuildingType.WITH_PARKING && monthlyRent.isBlank() ->
                ValidationError.MONTHLY_RENT_REQUIRED.key
            monthlyRent.toDoubleOrNull() == null -> ValidationError.MONTHLY_RENT_INVALID.key
            monthlyRent.toDouble() <= 0 -> ValidationError.MONTHLY_RENT_POSITIVE.key
            else -> null
        }

        _state.value = _state.value.copy(
            monthlyRent = monthlyRent,
            monthlyRentError = error
        )
    }

    fun saveBuilding() {
        val currentState = _state.value

        // Validate fields
        val addressError = if (currentState.address.isBlank()) ValidationError.ADDRESS_EMPTY.key else null
        val parkingSpacesError = when {
            currentState.buildingType == BuildingType.WITH_PARKING && currentState.parkingSpaces.isBlank() ->
                ValidationError.PARKING_SPACES_REQUIRED.key
            currentState.buildingType == BuildingType.WITH_PARKING && currentState.parkingSpaces.toIntOrNull() == null ->
                ValidationError.PARKING_SPACES_INVALID.key
            currentState.buildingType == BuildingType.WITH_PARKING && currentState.parkingSpaces.toInt() <= 0 ->
                ValidationError.PARKING_SPACES_POSITIVE.key
            else -> null
        }
        val monthlyRentError = when {
            currentState.buildingType == BuildingType.WITH_PARKING && currentState.monthlyRent.isBlank() ->
                ValidationError.MONTHLY_RENT_REQUIRED.key
            currentState.buildingType == BuildingType.WITH_PARKING && currentState.monthlyRent.toDoubleOrNull() == null ->
                ValidationError.MONTHLY_RENT_INVALID.key
            currentState.buildingType == BuildingType.WITH_PARKING && currentState.monthlyRent.toDouble() <= 0 ->
                ValidationError.MONTHLY_RENT_POSITIVE.key
            else -> null
        }

        if (addressError != null || parkingSpacesError != null || monthlyRentError != null) {
            _state.value = currentState.copy(
                addressError = addressError,
                parkingSpacesError = parkingSpacesError,
                monthlyRentError = monthlyRentError
            )
            _uiEvent.value = BuildingEditUiEvent.ShowError("validation_errors")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val building = when (currentState.buildingType) {
                    BuildingType.WITHOUT_PARKING -> Building.BuildingWithoutParking(
                        id = currentState.id ?: generateId(),
                        address = currentState.address
                    )
                    BuildingType.WITH_PARKING -> Building.BuildingWithParking(
                        id = currentState.id ?: generateId(),
                        address = currentState.address,
                        parkingSpaces = currentState.parkingSpaces.toInt(),
                        monthlyParkingRent = currentState.monthlyRent.toDouble()
                    )
                }

                if (currentState.isEditMode) {
                    updateBuildingUseCase(building)
                } else {
                    addBuildingUseCase(building)
                }

                _uiEvent.value = BuildingEditUiEvent.SaveSuccess
            } catch (e: Exception) {
                _uiEvent.value = BuildingEditUiEvent.ShowError("save_building_error")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun clearEvent() {
        _uiEvent.value = null
    }

    private fun generateId(): String = UUID.randomUUID().toString()
}

data class BuildingEditState(
    val id: String? = null,
    val address: String = "",
    val addressError: String? = null,
    val buildingType: BuildingType = BuildingType.WITHOUT_PARKING,
    val parkingSpaces: String = "",
    val parkingSpacesError: String? = null,
    val monthlyRent: String = "",
    val monthlyRentError: String? = null,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false
)

enum class BuildingType {
    WITHOUT_PARKING, WITH_PARKING
}

enum class ValidationError(val key: String) {
    ADDRESS_EMPTY("validation_address_empty"),
    PARKING_SPACES_REQUIRED("validation_parking_spaces_required"),
    PARKING_SPACES_INVALID("validation_parking_spaces_invalid"),
    PARKING_SPACES_POSITIVE("validation_parking_spaces_positive"),
    MONTHLY_RENT_REQUIRED("validation_monthly_rent_required"),
    MONTHLY_RENT_INVALID("validation_monthly_rent_invalid"),
    MONTHLY_RENT_POSITIVE("validation_monthly_rent_positive")
}

sealed class BuildingEditUiEvent {
    object SaveSuccess : BuildingEditUiEvent()
    data class ShowError(val errorKey: String) : BuildingEditUiEvent()
}