package com.example.cnc3d.core.network

import android.content.Context
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.example.cnc3d.domain.models.UsbDescriptor
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsbTransport(
    private val context: Context,
    private val scope: CoroutineScope,
    private val baudRate: Int = 115200,
    private val manualDescriptor: UsbDescriptor? = null
) : ConnectionTransport {

    override val type: ConnectionType = ConnectionType.USB

    private val _messages = MutableSharedFlow<String>()
    override val messages: Flow<String> = _messages

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var port: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null

    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager

            if (manualDescriptor != null) {
                return@withContext connectManual(manager, manualDescriptor)
            }

            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            if (availableDrivers.isEmpty()) return@withContext false

            val driver = availableDrivers[0]
            val usbConnection = manager.openDevice(driver.device) ?: return@withContext false
            connection = usbConnection

            val usbPort = driver.ports[0]
            usbPort.open(usbConnection)
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

    private suspend fun connectManual(manager: UsbManager, desc: UsbDescriptor): Boolean {
        val device = manager.deviceList.values.find {
            it.vendorId == desc.vendorId && it.productId == desc.productId
        } ?: return false

        val usbConnection = manager.openDevice(device) ?: return false
        connection = usbConnection

        // If we have endpoints, we can try to use them even if not in the database
        // But usb-serial-for-android needs a driver. 
        // We can wrap a custom driver if needed, but for now let's try the prober with custom logic

        val driver = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver != null) {
            val usbPort = driver.ports[0]
            usbPort.open(usbConnection)
            usbPort.setParameters(baudRate, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            port = usbPort
            _isConnected.value = true
            startListening()
            return true
        }

        return false
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
