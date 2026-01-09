// app/src/main/java/com/example/buildings/data/local/entity/BuildingEntity.kt
package com.example.buildings.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buildings")
data class BuildingEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "type")
    val type: String, // "WITHOUT_PARKING" or "WITH_PARKING"

    @ColumnInfo(name = "parking_spaces")
    val parkingSpaces: Int? = null,

    @ColumnInfo(name = "monthly_parking_rent")
    val monthlyParkingRent: Double? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)