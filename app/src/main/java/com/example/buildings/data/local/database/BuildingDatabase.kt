// app/src/main/java/com/example/buildings/data/local/database/BuildingDatabase.kt
package com.example.buildings.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.buildings.data.local.dao.BuildingDao
import com.example.buildings.data.local.entity.BuildingEntity

@Database(
    entities = [BuildingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BuildingDatabase : RoomDatabase() {
    abstract fun buildingDao(): BuildingDao

    companion object {
        const val DATABASE_NAME = "building_database"
    }
}