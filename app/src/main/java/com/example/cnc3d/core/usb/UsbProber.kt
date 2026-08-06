package com.example.cnc3d.core.usb

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.example.cnc3d.domain.models.UsbDescriptor

class UsbProber(private val context: Context) {

    fun scanDevices(): List<UsbDescriptor> {
        val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        return manager.deviceList.values.map { analyzeDevice(it) }
    }

    private fun analyzeDevice(device: UsbDevice): UsbDescriptor {
        var interfaceIndex = -1
        var inputEndpoint = -1
        var outputEndpoint = -1
        var transferType = -1

        // Look for the first compatible interface and endpoints
        for (i in 0 until device.interfaceCount) {
            val usbInterface = device.getInterface(i)
            // We search for Bulk or Interrupt endpoints commonly used for serial communication
            var foundIn = -1
            var foundOut = -1

            for (j in 0 until usbInterface.endpointCount) {
                val endpoint = usbInterface.getEndpoint(j)
                if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_BULK ||
                    endpoint.type == UsbConstants.USB_ENDPOINT_XFER_INT
                ) {

                    transferType = endpoint.type
                    if (endpoint.direction == UsbConstants.USB_DIR_IN) {
                        foundIn = endpoint.address
                    } else if (endpoint.direction == UsbConstants.USB_DIR_OUT) {
                        foundOut = endpoint.address
                    }
                }
            }

            if (foundIn != -1 && foundOut != -1) {
                interfaceIndex = i
                inputEndpoint = foundIn
                outputEndpoint = foundOut
                break
            }
        }

        return UsbDescriptor(
            vendorId = device.vendorId,
            productId = device.productId,
            deviceClass = device.deviceClass,
            deviceSubclass = device.deviceSubclass,
            deviceProtocol = device.deviceProtocol,
            interfaceIndex = interfaceIndex,
            inputEndpoint = inputEndpoint,
            outputEndpoint = outputEndpoint,
            transferType = transferType,
            deviceName = device.productName ?: "USB Device (${device.vendorId}:${device.productId})"
        )
    }
}
