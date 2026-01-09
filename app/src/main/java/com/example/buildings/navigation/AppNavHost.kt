package com.example.buildings.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.buildings.presentation.building_detail.BuildingDetailScreen
import com.example.buildings.presentation.building_edit.BuildingEditScreen
import com.example.buildings.presentation.buildings.BuildingsScreen
import com.example.buildings.presentation.tverskaya.TverskayaScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "buildings"
    ) {
        composable("buildings") {
            BuildingsScreen(
                onBuildingClick = { buildingId ->
                    navController.navigate("building_detail/$buildingId")
                },
                onAddBuilding = {
                    navController.navigate("building_edit")
                },
                onShowTverskaya = {
                    navController.navigate("tverskaya")
                }
            )
        }

        composable("tverskaya") {
            TverskayaScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("building_detail/{buildingId}") { backStackEntry ->
            val buildingId = backStackEntry.arguments?.getString("buildingId") ?: ""
            BuildingDetailScreen(
                buildingId = buildingId,
                onEditBuilding = { editBuildingId ->
                    navController.navigate("building_edit/$editBuildingId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("building_edit/{buildingId}") { backStackEntry ->
            val buildingId = backStackEntry.arguments?.getString("buildingId")
            BuildingEditScreen(
                buildingId = buildingId,
                onSaveComplete = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("building_edit") {
            BuildingEditScreen(
                buildingId = null,
                onSaveComplete = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}