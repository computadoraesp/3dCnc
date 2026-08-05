package com.example.cnc3d.core.network

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlinx.coroutines.CoroutineScope

class BluetoothTransport(
    private val context: Context,
    private val scope: CoroutineScope,
    private val deviceAddress: String
) : ConnectionTransport {

    override val type: ConnectionType = ConnectionType.BLUETOOTH

    private val _messages = MutableSharedFlow<String>()
    override val messages: Flow<String> = _messages

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SPP

    @SuppressLint("MissingPermission")
    override suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = bluetoothManager.adapter ?: return@withContext false
            val device = adapter.getRemoteDevice(deviceAddress)

            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket?.connect()
            
            inputStream = socket?.inputStream
            outputStream = socket?.outputStream
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
                    val bytes = inputStream?.read(buffer) ?: -1
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
        inputStream?.close()
        outputStream?.close()
        socket?.close()
        inputStream = null
        outputStream = null
        socket = null
    }

    override suspend fun send(message: String): Boolean = withContext(Dispatchers.IO) {
        try {
            outputStream?.write(message.toByteArray())
            outputStream?.flush()
            true
        } catch (e: Exception) {
            false
        }
    }
}
