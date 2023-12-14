package com.adorastudios.androidbluetoothmessenger.domain

import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDeviceData>>
    val pairedDevices: StateFlow<List<BluetoothDeviceData>>

    fun startDiscovery()
    fun stopDiscovery()

    fun release()
}
