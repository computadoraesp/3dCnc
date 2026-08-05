package com.example.cnc3d.di

import com.example.cnc3d.core.api.fluidnc.FluidncApiService
import com.example.cnc3d.core.api.moonraker.MoonrakerApiService
import com.example.cnc3d.core.network.ConnectionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DriverModule {

    @Provides
    @Singleton
    fun provideFluidncApiService(connectionManager: ConnectionManager): FluidncApiService {
        return FluidncApiService(connectionManager)
    }

    @Provides
    @Singleton
    fun provideMoonrakerApiService(connectionManager: ConnectionManager): MoonrakerApiService {
        return MoonrakerApiService(connectionManager)
    }
}
