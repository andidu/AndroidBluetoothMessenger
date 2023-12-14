package com.adorastudios.androidbluetoothmessenger.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

data class BluetoothDeviceData(
    val name: String?,
    val address: String,
) {
    companion object {
        @SuppressLint("MissingPermission")
        fun BluetoothDevice.toData() = BluetoothDeviceData(name = this.name, address = this.address)
    }
}
