package com.adorastudios.androidbluetoothmessenger.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<BluetoothDeviceData>>
    val pairedDevices: StateFlow<List<BluetoothDeviceData>>

    fun startDiscovery()
    fun stopDiscovery()

    fun startService(device: BluetoothDeviceData): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDeviceData): Flow<ConnectionResult>
    fun closeConnection()

    fun release()
}
