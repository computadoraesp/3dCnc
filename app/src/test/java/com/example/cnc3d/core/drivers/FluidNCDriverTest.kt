package com.example

import com.example.cnc3d.core.api.fluidnc.FluidncApiService

.cnc3d.core.drivers

.cnc3d.core.api.fluidnc.FluidncApiService
.cnc3d.core.network.ConnectionManager
import com.example.cnc3d.core.drivers.FluidNCDriver
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FluidNCDriverTest {

    private val api = mockk<FluidncApiService>()
    private val connectionManager = mockk<ConnectionManager>()
    private val scope = CoroutineScope(Dispatchers.Unconfined)
    private val messages = MutableSharedFlow<String>()

    @Test
    fun `parseGrblStatus should correctly parse GRBL status message`() = runTest {
        every { connectionManager.webSocketMessages() } returns messages
        
        val driver = FluidNCDriver(api, connectionManager, scope)
        
        messages.emit("<Idle|WPos:10.0,20.0,30.0|Bf:15,128|FS:500,8000>")
        
        val status = driver.status.value
        assertEquals("Idle", status.state)
        assertEquals(10f, status.position.first)
        assertEquals(20f, status.position.second)
        assertEquals(30f, status.position.third)
        assertEquals(500, status.feedRate)
        assertEquals(8000, status.spindleRpm)
    }

    @Test
    fun `parseMessage should correctly parse JSON status message`() = runTest {
        every { connectionManager.webSocketMessages() } returns messages
        
        val driver = FluidNCDriver(api, connectionManager, scope)
        
        val json = """
            {
                "status": {
                    "state": "Run",
                    "pos": [1.5, 2.5, 3.5],
                    "fs": [1000, 12000]
                }
            }
        """.trimIndent()
        
        messages.emit(json)
        
        val status = driver.status.value
        assertEquals("Run", status.state)
        assertEquals(1.5f, status.position.first)
        assertEquals(2.5f, status.position.second)
        assertEquals(3.5f, status.position.third)
        assertEquals(1000, status.feedRate)
        assertEquals(12000, status.spindleRpm)
    }
}
