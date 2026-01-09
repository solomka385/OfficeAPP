package com.example.buildings.presentation.building_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buildings.ui.strings.*
import com.example.core.domain.model.Building
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingDetailScreen(
    buildingId: String,
    onEditBuilding: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: BuildingDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(buildingId) {
        viewModel.loadBuilding(buildingId)
    }

    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(buildingDetails()) },  // Используем функцию из Strings.kt
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = back()  // Используем функцию из Strings.kt
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditBuilding(buildingId) }) {
                        Icon(Icons.Default.Edit, contentDescription = edit())  // Используем функцию из Strings.kt
                    }
                    IconButton(onClick = {
                        viewModel.deleteBuilding()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = delete())  // Используем функцию из Strings.kt
                    }
                }
            )
        }
    ) { padding ->
        when (state) {
            is BuildingDetailState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is BuildingDetailState.Success -> {
                val building = state.building
                val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
                    maximumFractionDigits = 2
                    minimumFractionDigits = 0
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = building.address,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            when (building) {
                                is Building.BuildingWithoutParking -> {
                                    Text(typeWithoutParking())  // Используем функцию из Strings.kt
                                }
                                is Building.BuildingWithParking -> {
                                    Text(typeWithParking())  // Используем функцию из Strings.kt
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(parkingSpaces(building.parkingSpaces))  // Используем функцию из Strings.kt
                                    Text(monthlyRent(numberFormat.format(building.monthlyParkingRent)))  // Используем функцию из Strings.kt
                                }
                            }
                        }
                    }
                }
            }
            is BuildingDetailState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message)  // Оставьте как есть, или локализуйте ошибки
                }
            }
        }
    }
}