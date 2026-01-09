// core/src/main/kotlin/com/example/core/domain/model/Building.kt
package com.example.core.domain.model

sealed class Building {
    abstract val id: String
    abstract val address: String

    data class BuildingWithoutParking(
        override val id: String,
        override val address: String
    ) : Building()

    data class BuildingWithParking(
        override val id: String,
        override val address: String,
        val parkingSpaces: Int,
        val monthlyParkingRent: Double
    ) : Building()
}