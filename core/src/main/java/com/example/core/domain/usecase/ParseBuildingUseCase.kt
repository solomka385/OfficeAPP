// core/src/main/kotlin/com/example/core/domain/usecase/ParseBuildingUseCase.kt
package com.example.core.domain.usecase

import com.example.core.domain.model.Building
import java.util.UUID

class ParseBuildingUseCase {
    operator fun invoke(input: String): Result<Building> {
        return try {
            val parts = input.split(";").map { it.trim() }

            when {
                parts.isEmpty() -> Result.failure(
                    IllegalArgumentException("Введите адрес здания / Enter building address")
                )
                parts.size == 1 -> {
                    // Здание без парковки
                    val address = parts[0]
                    if (address.isBlank()) {
                        return Result.failure(IllegalArgumentException("Адрес не может быть пустым / Address cannot be empty"))
                    }
                    val id = UUID.randomUUID().toString()
                    Result.success(Building.BuildingWithoutParking(id, address))
                }
                parts.size == 3 -> {
                    // Здание с парковкой
                    val address = parts[0]
                    val parkingSpaces = parts[1].toIntOrNull()
                    val monthlyRent = parts[2].toDoubleOrNull()

                    if (address.isBlank()) {
                        return Result.failure(IllegalArgumentException("Адрес не может быть пустым / Address cannot be empty"))
                    }
                    if (parkingSpaces == null || parkingSpaces <= 0) {
                        return Result.failure(IllegalArgumentException("Некорректное количество машиномест / Invalid number of parking spaces"))
                    }
                    if (monthlyRent == null || monthlyRent <= 0) {
                        return Result.failure(IllegalArgumentException("Некорректная стоимость аренды / Invalid monthly rent"))
                    }

                    val id = UUID.randomUUID().toString()
                    Result.success(
                        Building.BuildingWithParking(id, address, parkingSpaces, monthlyRent)
                    )
                }
                else -> Result.failure(
                    IllegalArgumentException("Неверный формат. Используйте: адрес (для здания без парковки) или адрес;количество_машиномест;стоимость_аренды (для здания с парковки) / Invalid format. Use: address (for building without parking) or address;parking_spaces;monthly_rent (for building with parking)")
                )
            }
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Ошибка парсинга: ${e.message} / Parsing error: ${e.message}"))
        }
    }
}