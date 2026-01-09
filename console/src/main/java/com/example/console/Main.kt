// console/src/main/kotlin/com/example/buildings/console/Main.kt
package com.example.buildings.console

import com.example.buildings.console.data.repository.ConsoleBuildingRepository
import com.example.core.domain.model.Building
import com.example.core.domain.usecase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.*

fun main() = runBlocking {
    println("=== Office Buildings Management Console App ===")
    println("Commands: add, list, get, search, update, delete, help, exit")

    // Создаем зависимости вручную (без DI, так как это консольное приложение)
    val repository = ConsoleBuildingRepository()
    val getAllBuildingsUseCase = GetAllBuildingsUseCase(repository)
    val getBuildingByIdUseCase = GetBuildingByIdUseCase(repository)
    val addBuildingUseCase = AddBuildingUseCase(repository)
    val updateBuildingUseCase = UpdateBuildingUseCase(repository)
    val deleteBuildingUseCase = DeleteBuildingUseCase(repository)
    val parseBuildingUseCase = ParseBuildingUseCase()
    val searchBuildingsUseCase = GetBuildingsByAddressUseCase(repository)

    val scanner = Scanner(System.`in`)
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    while (true) {
        print("\n> ")
        val command = scanner.nextLine().trim()

        when {
            command == "exit" -> {
                println("Goodbye!")
                break
            }
            command == "list" -> {
                val buildings = getAllBuildingsUseCase().first()
                if (buildings.isEmpty()) {
                    println("No buildings found")
                } else {
                    println("=== All Buildings ===")
                    buildings.forEachIndexed { index, building ->
                        println("${index + 1}. ${formatBuilding(building, numberFormat)}")
                    }
                }
            }
            command.startsWith("add ") -> {
                val input = command.substring(4).trim()
                val result = parseBuildingUseCase(input)
                result.fold(
                    onSuccess = { building ->
                        try {
                            addBuildingUseCase(building)
                            println("Building added successfully: ${formatBuilding(building, numberFormat)}")
                        } catch (e: Exception) {
                            println("Error: ${e.message}")
                        }
                    },
                    onFailure = { error ->
                        println("Error: ${error.message}")
                    }
                )
            }
            command.startsWith("get ") -> {
                val id = command.substring(4).trim()
                val building = getBuildingByIdUseCase(id)
                if (building != null) {
                    println(formatBuilding(building, numberFormat))
                } else {
                    println("Building with id '$id' not found")
                }
            }
            command.startsWith("search ") -> {
                val query = command.substring(7).trim()
                val buildings = searchBuildingsUseCase(query)
                if (buildings.isEmpty()) {
                    println("No buildings found for query: '$query'")
                } else {
                    println("=== Search Results for '$query' ===")
                    buildings.forEachIndexed { index, building ->
                        println("${index + 1}. ${formatBuilding(building, numberFormat)}")
                    }
                }
            }
            command.startsWith("update ") -> {
                // Обновление здания: update <id> <new_address> [parking_spaces] [monthly_rent]
                val parts = command.substring(7).trim().split(" ")
                if (parts.size >= 2) {
                    val id = parts[0]
                    val building = getBuildingByIdUseCase(id)
                    if (building != null) {
                        val newAddress = parts[1]
                        val updatedBuilding = when (building) {
                            is Building.BuildingWithoutParking -> {
                                if (parts.size == 4) {
                                    // Преобразуем в здание с парковкой
                                    val parkingSpaces = parts[2].toIntOrNull()
                                    val monthlyRent = parts[3].toDoubleOrNull()
                                    if (parkingSpaces != null && monthlyRent != null) {
                                        Building.BuildingWithParking(
                                            id = building.id,
                                            address = newAddress,
                                            parkingSpaces = parkingSpaces,
                                            monthlyParkingRent = monthlyRent
                                        )
                                    } else {
                                        println("Invalid parking spaces or monthly rent format")
                                        null
                                    }
                                } else {
                                    building.copy(address = newAddress)
                                }
                            }
                            is Building.BuildingWithParking -> {
                                if (parts.size == 4) {
                                    val parkingSpaces = parts[2].toIntOrNull()
                                    val monthlyRent = parts[3].toDoubleOrNull()
                                    if (parkingSpaces != null && monthlyRent != null) {
                                        building.copy(
                                            address = newAddress,
                                            parkingSpaces = parkingSpaces,
                                            monthlyParkingRent = monthlyRent
                                        )
                                    } else {
                                        println("Invalid parking spaces or monthly rent format")
                                        null
                                    }
                                } else {
                                    // Если не указаны параметры парковки, оставляем старые значения
                                    building.copy(address = newAddress)
                                }
                            }
                        }

                        if (updatedBuilding != null) {
                            updateBuildingUseCase(updatedBuilding)
                            println("Building updated successfully")
                        }
                    } else {
                        println("Building with id '$id' not found")
                    }
                } else {
                    println("Usage: update <id> <new_address> [parking_spaces monthly_rent]")
                }
            }
            command.startsWith("delete ") -> {
                val id = command.substring(7).trim()
                val building = getBuildingByIdUseCase(id)
                if (building != null) {
                    deleteBuildingUseCase(building)
                    println("Building deleted successfully")
                } else {
                    println("Building with id '$id' not found")
                }
            }
            command == "help" -> {
                printHelp()
            }
            command == "tverskaya" -> {
                // Специальная команда для поиска зданий на Тверской
                println("=== Buildings on Tverskaya ===")
                val tverskayaBuildings = searchBuildingsUseCase("Тверская")
                if (tverskayaBuildings.isEmpty()) {
                    println("No buildings found on Tverskaya")
                } else {
                    tverskayaBuildings.forEachIndexed { index, building ->
                        println("${index + 1}. ${formatBuilding(building, numberFormat)}")
                        // Выводим дополнительную информацию о парковке для зданий на Тверской
                        if (building is Building.BuildingWithParking) {
                            println("   Monthly parking revenue: ${numberFormat.format(building.parkingSpaces * building.monthlyParkingRent)}")
                        }
                        println()
                    }
                }
            }
            else -> {
                println("Unknown command. Type 'help' for available commands.")
            }
        }
    }
}

private fun formatBuilding(building: Building, numberFormat: NumberFormat): String {
    return when (building) {
        is Building.BuildingWithoutParking -> {
            "ID: ${building.id}\n" +
                "Address: ${building.address}\n" +
                "Type: Building without parking"
        }
        is Building.BuildingWithParking -> {
            "ID: ${building.id}\n" +
                "Address: ${building.address}\n" +
                "Type: Building with parking\n" +
                "Parking spaces: ${building.parkingSpaces}\n" +
                "Monthly rent per space: ${numberFormat.format(building.monthlyParkingRent)}\n" +
                "Total monthly parking revenue: ${numberFormat.format(building.parkingSpaces * building.monthlyParkingRent)}"
        }
    }
}

private fun printHelp() {
    println("""
        ========================================
        Office Buildings Management System
        ========================================
        Available commands:

        1. add <address> - Add building without parking
           Example: add "Тверская ул., 25"

        2. add <address>;<parking_spaces>;<monthly_rent> - Add building with parking
           Example: add "Тверская ул., 25";100;5000.0

        3. list - List all buildings

        4. get <id> - Get building by ID

        5. search <query> - Search buildings by address

        6. tverskaya - Show all buildings on Tverskaya street
           (Special command for task requirement)

        7. update <id> <new_address> [parking_spaces monthly_rent] - Update building
           Examples:
           - update abc123 "Новый адрес, 10"
           - update abc123 "Новый адрес, 10" 120 6000.0

        8. delete <id> - Delete building by ID

        9. help - Show this help

        10. exit - Exit the application

        ========================================
        Формат ввода для добавления здания:
        • Без парковки: адрес
        • С парковкой: адрес;количество_машиномест;стоимость_аренды

        Примеры:
        • add "Тверская ул., 25"
        • add "Тверская ул., 25";100;5000.0
        • add "ул. Пушкина, 10";50;3000.0

        Задача: Вывести здания, в адресе которых есть слово «Тверская».
        Используйте команду: tverskaya
        ========================================
    """.trimIndent())
}
