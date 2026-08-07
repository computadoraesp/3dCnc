package com.example.cnc3d.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.core.network.ConnectionState
import com.example.cnc3d.domain.usecases.GetStatusUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MachineMonitoringService : Service() {

    @Inject
    lateinit var connectionManager: ConnectionManager

    @Inject
    lateinit var getStatusUseCase: GetStatusUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitoringJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification("Monitoring Machine..."))

        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (isActive) {
                if (connectionManager.state.value == ConnectionState.Connected) {
                    try {
                        getStatusUseCase()
                    } catch (e: Exception) {
                        // Handle polling error
                    }
                }
                delay(2000)
            }
        }

        return START_STICKY
    }

    private fun createNotification(text: String) =
        NotificationCompat.Builder(this, "machine_monitoring")
            .setContentTitle("3dCnc Live")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Placeholder
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        monitoringJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}
