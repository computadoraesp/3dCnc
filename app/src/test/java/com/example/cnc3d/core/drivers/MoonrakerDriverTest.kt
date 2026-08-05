package com.example.cnc3d.core.drivers

import com.example.cnc3d.core.api.moonraker.MoonrakerApiService
import com.example.cnc3d.core.network.ConnectionManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MoonrakerDriverTest {

    private val api = mockk<MoonrakerApiService>()
    private val connectionManager = mockk<ConnectionManager>()
    private val scope = CoroutineScope(Dispatchers.Unconfined)
    private val messages = MutableSharedFlow<String>()

    @Test
    fun `parseNotification should correctly parse Moonraker status update`() = runTest {
        every { connectionManager.webSocketMessages() } returns messages
        
        val driver = MoonrakerDriver(api, connectionManager, scope)
        
        val notification = """
            {
                "method": "notify_status_update",
                "params": [
                    {
                        "extruder": { "temperature": 210.5 },
                        "heater_bed": { "temperature": 60.0 },
                        "virtual_sdcard": { "progress": 0.45 },
                        "print_stats": { "state": "printing" }
                    }
                ]
            }
        """.trimIndent()
        
        messages.emit(notification)
        
        val status = driver.status.value
        assertEquals("printing", status.state)
        assertEquals(210.5f, status.temperatureHotend)
        assertEquals(60.0f, status.temperatureBed)
        assertEquals(45f, status.progress)
    }
}
