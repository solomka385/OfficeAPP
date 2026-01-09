// app/src/main/java/com/example/buildings/di/AppModule.kt
package com.example.buildings.di

import android.content.Context
import androidx.room.Room
import com.example.core.domain.repository.BuildingRepository
import com.example.core.domain.usecase.*
import com.example.buildings.data.local.database.BuildingDatabase
import com.example.buildings.data.mapper.BuildingMapper
import com.example.buildings.data.repository.RoomBuildingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBuildingDatabase(@ApplicationContext context: Context): BuildingDatabase {
        return Room.databaseBuilder(
            context,
            BuildingDatabase::class.java,
            BuildingDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideBuildingDao(database: BuildingDatabase) = database.buildingDao()

    @Provides
    @Singleton
    fun provideBuildingMapper(): BuildingMapper = BuildingMapper()

    @Provides
    @Singleton
    fun provideBuildingRepository(
        dao: com.example.buildings.data.local.dao.BuildingDao,
        mapper: BuildingMapper
    ): BuildingRepository {
        return RoomBuildingRepository(dao, mapper)
    }

    @Provides
    @Singleton
    fun provideGetAllBuildingsUseCase(
        repository: BuildingRepository
    ): GetAllBuildingsUseCase = GetAllBuildingsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetBuildingByIdUseCase(
        repository: BuildingRepository
    ): GetBuildingByIdUseCase = GetBuildingByIdUseCase(repository)

    @Provides
    @Singleton
    fun provideAddBuildingUseCase(
        repository: BuildingRepository
    ): AddBuildingUseCase = AddBuildingUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateBuildingUseCase(
        repository: BuildingRepository
    ): UpdateBuildingUseCase = UpdateBuildingUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteBuildingUseCase(
        repository: BuildingRepository
    ): DeleteBuildingUseCase = DeleteBuildingUseCase(repository)

    @Provides
    @Singleton
    fun provideParseBuildingUseCase(): ParseBuildingUseCase = ParseBuildingUseCase()

    @Provides
    @Singleton
    fun provideGetBuildingsByAddressUseCase(
        repository: BuildingRepository
    ): GetBuildingsByAddressUseCase = GetBuildingsByAddressUseCase(repository)
}