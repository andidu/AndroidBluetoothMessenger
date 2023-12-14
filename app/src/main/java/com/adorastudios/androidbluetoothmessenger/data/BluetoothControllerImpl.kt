package com.adorastudios.androidbluetoothmessenger.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothController
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData.Companion.toData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BluetoothControllerImpl(private val context: Context) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceData>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceData>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceData>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceData>>
        get() = _pairedDevices.asStateFlow()

    private val newDeviceReceiver = NewDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val deviceData = device.toData()
            if (deviceData in devices) devices else devices + deviceData
        }
    }

    init {
        updatePairedDevices()
    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery() = runIfHasScanPermission {
        context.registerReceiver(
            newDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND),
        )

        updatePairedDevices()

        bluetoothAdapter.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopDiscovery() = runIfHasScanPermission {
        bluetoothAdapter.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(newDeviceReceiver)
    }

    @SuppressLint("MissingPermission")
    private fun updatePairedDevices() = runIfHasConnectPermission {
        _pairedDevices.update {
            bluetoothAdapter.bondedDevices.map { it.toData() }
        }
    }

    private inline fun runIfHasScanPermission(block: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_SCAN,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            block()
        }
    }

    private inline fun runIfHasConnectPermission(block: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            block()
        }
    }
}
