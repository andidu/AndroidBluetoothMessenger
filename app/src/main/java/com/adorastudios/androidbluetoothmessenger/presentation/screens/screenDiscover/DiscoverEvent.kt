package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover

import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData

sealed class DiscoverEvent {
    object StartScan : DiscoverEvent()
    object StopScan : DiscoverEvent()
    data class ConnectToServer(val data: BluetoothDeviceData) : DiscoverEvent()
    data class ConnectToClient(val data: BluetoothDeviceData) : DiscoverEvent()
    object StopConnection : DiscoverEvent()
}
