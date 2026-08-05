package com.example.cnc3d.core.network

import android.content.Context
import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope

class UsbTransport(
    private val context: Context,
    private val scope: CoroutineScope,
    private val baudRate: Int = 115200
) : ConnectionTransport {

    override val type: ConnectionType = ConnectionType.USB

    private val _messages = MutableSharedFlow<String>()
    override val messages: Flow<String> = _messages

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var port: UsbSerialPort? = null

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            if (availableDrivers.isEmpty()) return@withContext false

            val driver = availableDrivers[0]
            val connection = manager.openDevice(driver.device) ?: return@withContext false

            val usbPort = driver.ports[0]
            usbPort.open(connection)
            usbPort.setParameters(baudRate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            
            port = usbPort
            _isConnected.value = true

            startListening()
            true
        } catch (e: Exception) {
            _isConnected.value = false
            false
        }
    }

    private fun startListening() {
        scope.launch(Dispatchers.IO) {
            val buffer = ByteArray(1024)
            while (isActive && _isConnected.value) {
                try {
                    val bytes = port?.read(buffer, 1000) ?: 0
                    if (bytes > 0) {
                        val message = String(buffer, 0, bytes)
                        _messages.emit(message)
                    }
                } catch (e: Exception) {
                    _isConnected.value = false
                    break
                }
            }
        }
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        _isConnected.value = false
        port?.close()
        port = null
    }

    override suspend fun send(message: String): Boolean = withContext(Dispatchers.IO) {
        try {
            port?.write(message.toByteArray(), 1000)
            true
        } catch (e: Exception) {
            false
        }
    }
}
