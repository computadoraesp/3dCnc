package com.example.cnc3d.di

import android.content.Context
import com.example.cnc3d.data.repositories.MachineProfileRepositoryImpl
import com.example.cnc3d.datastore.MachineProfileStore
import com.example.cnc3d.domain.repositories.MachineProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideMachineProfileStore(@ApplicationContext context: Context): MachineProfileStore {
        return MachineProfileStore(context)
    }

    @Provides
    @Singleton
    fun provideMachineProfileRepository(store: MachineProfileStore): MachineProfileRepository {
        return MachineProfileRepositoryImpl(store)
    }
}
