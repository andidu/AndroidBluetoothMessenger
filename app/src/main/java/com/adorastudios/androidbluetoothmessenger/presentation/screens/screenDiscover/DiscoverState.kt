package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover

import androidx.compose.runtime.Immutable
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData

@Immutable
data class DiscoverState(
    val scannedDevices: List<BluetoothDeviceData> = emptyList(),
    val pairedDevices: List<BluetoothDeviceData> = emptyList(),
)
