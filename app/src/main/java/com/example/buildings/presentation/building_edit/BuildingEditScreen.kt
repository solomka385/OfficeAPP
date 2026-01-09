// app/src/main/java/com/example/buildings/presentation/building_edit/BuildingEditScreen.kt
package com.example.buildings.presentation.building_edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buildings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingEditScreen(
    buildingId: String?,
    onSaveComplete: () -> Unit,
    onCancel: () -> Unit,
    viewModel: BuildingEditViewModel = hiltViewModel()
) {
    LaunchedEffect(buildingId) {
        viewModel.loadBuilding(buildingId)
    }

    val state = viewModel.state.collectAsState().value
    val uiEvent = viewModel.uiEvent.collectAsState().value

    LaunchedEffect(uiEvent) {
        when (uiEvent) {
            is BuildingEditUiEvent.SaveSuccess -> onSaveComplete()
            is BuildingEditUiEvent.ShowError -> {

            }
            null -> {}
        }
        if (uiEvent != null) {
            viewModel.clearEvent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditMode) stringResource(R.string.edit_building_title)
                        else stringResource(R.string.add_building_title)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveBuilding() },
                        enabled = !state.isLoading,
                        modifier = Modifier.testTag("save_button")
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.testTag("loading_indicator"))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.address,
                    onValueChange = viewModel::updateAddress,
                    label = { Text(stringResource(R.string.building_address)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("address_field"),
                    isError = state.addressError != null,
                    supportingText = {
                        if (state.addressError != null) {
                            Text(text = getTranslatedError(state.addressError))
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("building_type_selector"),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilterChip(
                        selected = state.buildingType == BuildingType.WITHOUT_PARKING,
                        onClick = { viewModel.updateBuildingType(BuildingType.WITHOUT_PARKING) },
                        label = { Text(stringResource(R.string.without_parking)) },
                        modifier = Modifier.testTag("without_parking_chip")
                    )
                    FilterChip(
                        selected = state.buildingType == BuildingType.WITH_PARKING,
                        onClick = { viewModel.updateBuildingType(BuildingType.WITH_PARKING) },
                        label = { Text(stringResource(R.string.with_parking)) },
                        modifier = Modifier.testTag("with_parking_chip")
                    )
                }

                if (state.buildingType == BuildingType.WITH_PARKING) {
                    OutlinedTextField(
                        value = state.parkingSpaces,
                        onValueChange = viewModel::updateParkingSpaces,
                        label = { Text(stringResource(R.string.parking_spaces)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parking_spaces_field"),
                        isError = state.parkingSpacesError != null,
                        supportingText = {
                            if (state.parkingSpacesError != null) {
                                Text(text = getTranslatedError(state.parkingSpacesError))
                            }
                        }
                    )

                    OutlinedTextField(
                        value = state.monthlyRent,
                        onValueChange = viewModel::updateMonthlyRent,
                        label = { Text(stringResource(R.string.monthly_rent)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("monthly_rent_field"),
                        isError = state.monthlyRentError != null,
                        supportingText = {
                            if (state.monthlyRentError != null) {
                                Text(text = getTranslatedError(state.monthlyRentError))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun getTranslatedError(errorKey: String?): String {
    return when (errorKey) {
        ValidationError.ADDRESS_EMPTY.key -> stringResource(R.string.validation_address_empty)
        ValidationError.PARKING_SPACES_REQUIRED.key -> stringResource(R.string.validation_parking_spaces_required)
        ValidationError.PARKING_SPACES_INVALID.key -> stringResource(R.string.validation_parking_spaces_invalid)
        ValidationError.PARKING_SPACES_POSITIVE.key -> stringResource(R.string.validation_parking_spaces_positive)
        ValidationError.MONTHLY_RENT_REQUIRED.key -> stringResource(R.string.validation_monthly_rent_required)
        ValidationError.MONTHLY_RENT_INVALID.key -> stringResource(R.string.validation_monthly_rent_invalid)
        ValidationError.MONTHLY_RENT_POSITIVE.key -> stringResource(R.string.validation_monthly_rent_positive)
        "building_not_found" -> stringResource(R.string.error_building_not_found)
        "load_building_error" -> stringResource(R.string.error_load_building)
        "save_building_error" -> stringResource(R.string.error_save_building)
        "validation_errors" -> stringResource(R.string.error_validation_errors)
        else -> errorKey ?: ""
    }
}