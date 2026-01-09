package com.example.buildings.di

import com.example.buildings.data.repository.FakeBuildingRepository
import com.example.core.domain.repository.BuildingRepository
import com.example.core.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [com.example.buildings.di.AppModule::class]
)
@Module
object TestAppModule {

    @Provides
    @Singleton
    fun provideBuildingRepository(): BuildingRepository {
        return FakeBuildingRepository()
    }

    @Provides
    @Singleton
    fun provideGetAllBuildingsUseCase(repository: BuildingRepository): GetAllBuildingsUseCase {
        return GetAllBuildingsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBuildingByIdUseCase(repository: BuildingRepository): GetBuildingByIdUseCase {
        return GetBuildingByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddBuildingUseCase(repository: BuildingRepository): AddBuildingUseCase {
        return AddBuildingUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateBuildingUseCase(repository: BuildingRepository): UpdateBuildingUseCase {
        return UpdateBuildingUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteBuildingUseCase(repository: BuildingRepository): DeleteBuildingUseCase {
        return DeleteBuildingUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBuildingsByAddressUseCase(repository: BuildingRepository): GetBuildingsByAddressUseCase {
        return GetBuildingsByAddressUseCase(repository)
    }
}