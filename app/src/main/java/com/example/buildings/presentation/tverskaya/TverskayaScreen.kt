package com.example.buildings.presentation.tverskaya

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buildings.presentation.buildings.BuildingsViewModel
import com.example.buildings.ui.strings.*
import com.example.core.domain.model.Building
import java.text.NumberFormat
import java.util.*

    @OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TverskayaScreen(
    onNavigateBack: () -> Unit,
    viewModel: BuildingsViewModel = hiltViewModel()
) {
    var tverskayaBuildings by remember { mutableStateOf<List<Building>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            tverskayaBuildings = viewModel.getTverskayaBuildings()
        } catch (e: Exception) {
            // Обработка ошибки
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Здания на Тверской") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = back())
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (tverskayaBuildings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет зданий на Тверской")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tverskayaBuildings, key = { it.id }) { building ->
                    TverskayaBuildingItem(building = building)
                }
            }
        }
    }
}

@Composable
fun TverskayaBuildingItem(building: Building) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = building.address,
                style = MaterialTheme.typography.headlineSmall
            )
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