// app/src/main/java/com/example/buildings/data/local/dao/BuildingDao.kt
package com.example.buildings.data.local.dao

import androidx.room.*
import com.example.buildings.data.local.entity.BuildingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {
    @Query("SELECT * FROM buildings ORDER BY created_at DESC")
    fun getAll(): Flow<List<BuildingEntity>>

    @Query("SELECT * FROM buildings WHERE id = :id")
    suspend fun getById(id: String): BuildingEntity?

    @Query("SELECT * FROM buildings WHERE address LIKE '%' || :query || '%'")
    suspend fun searchByAddress(query: String): List<BuildingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(building: BuildingEntity)

    @Update
    suspend fun update(building: BuildingEntity)

    @Delete
    suspend fun delete(building: BuildingEntity)

    @Query("DELETE FROM buildings")
    suspend fun deleteAll()
}