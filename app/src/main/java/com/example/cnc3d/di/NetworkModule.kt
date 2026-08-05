package com.example.cnc3d.di

import android.content.Context
import com.example.cnc3d.core.discovery.BluetoothScanner
import com.example.cnc3d.core.discovery.NetworkScanner
import com.example.cnc3d.core.discovery.WifiScanner
import com.example.cnc3d.core.network.ConnectionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun provideConnectionManager(scope: CoroutineScope): ConnectionManager = ConnectionManager(scope)

    @Provides
    @Singleton
    fun provideNetworkScanner(@ApplicationContext context: Context): NetworkScanner = NetworkScanner(context)

    @Provides
    @Singleton
    fun provideBluetoothScanner(@ApplicationContext context: Context): BluetoothScanner = BluetoothScanner(context)

    @Provides
    @Singleton
    fun provideWifiScanner(@ApplicationContext context: Context): WifiScanner = WifiScanner(context)
}
