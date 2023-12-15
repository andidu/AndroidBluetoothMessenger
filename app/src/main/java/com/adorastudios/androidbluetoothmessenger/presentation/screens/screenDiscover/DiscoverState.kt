package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover

import androidx.compose.runtime.Immutable
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData
import com.adorastudios.androidbluetoothmessenger.domain.Message

@Immutable
data class DiscoverState(
    val scannedDevices: List<BluetoothDeviceData> = emptyList(),
    val pairedDevices: List<BluetoothDeviceData> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val connectionName: String? = null,
    val server: Boolean = false,
    val messages: List<Message> = emptyList(),
)
