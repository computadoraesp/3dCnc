package com.example.cnc3d.core.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkScanner(private val context: Context) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    fun scanServices(serviceTypes: List<String> = listOf("_http._tcp.", "_moonraker._tcp.", "_fluidnc._tcp.")): Flow<NsdServiceInfo> = callbackFlow {
        
        val listeners = serviceTypes.map { type ->
            val discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String) {}

                override fun onServiceFound(service: NsdServiceInfo) {
                    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            trySend(serviceInfo)
                        }
                    })
                }

                override fun onServiceLost(service: NsdServiceInfo) {}
                override fun onDiscoveryStopped(regType: String) {}
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {}
                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
            }
            
            nsdManager.discoverServices(type, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            discoveryListener
        }

        awaitClose {
            listeners.forEach { listener ->
                nsdManager.stopServiceDiscovery(listener)
            }
        }
    }
}
