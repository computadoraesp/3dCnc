package com.example.cnc3d.di

import com.example.cnc3d.data.repositories.MachineRepositoryImpl
import com.example.cnc3d.domain.repositories.MachineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMachineRepository(impl: MachineRepositoryImpl): MachineRepository
}
