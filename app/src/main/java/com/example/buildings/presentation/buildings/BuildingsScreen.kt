package com.example.buildings.presentation.buildings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.buildings.ui.strings.*
import com.example.core.domain.model.Building
import java.text.NumberFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingsScreen(
    onBuildingClick: (String) -> Unit,
    onAddBuilding: () -> Unit,
    onShowTverskaya: () -> Unit,
    viewModel: BuildingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(buildingsTitle())
                },
                actions = {
                    // Простая кнопка для показа зданий на Тверской
                    IconButton(
                        onClick = onShowTverskaya,
                        modifier = Modifier.testTag("tverskaya_button")
                    ) {
                        Icon(
                            Icons.Default.FilterAlt,
                            contentDescription = "Показать здания на Тверской"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBuilding,
                modifier = Modifier.testTag("add_building_button")
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = addBuilding()
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            noBuildings(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            tapToAdd(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state, key = { it.id }) { building ->
                        BuildingItem(
                            building = building,
                            onClick = { onBuildingClick(building.id) },
                            onDelete = { viewModel.deleteBuilding(building) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingItem(
    building: Building,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = building.address,
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = delete())
                }
            }
            when (building) {
                is Building.BuildingWithoutParking -> {
                    Text(typeWithoutParking())
                }
                is Building.BuildingWithParking -> {
                    Column {
                        Text(typeWithParking())
                        Text(parkingSpaces(building.parkingSpaces))
                        Text(monthlyRent(numberFormat.format(building.monthlyParkingRent)))
                    }
                }
            }
        }
    }
}