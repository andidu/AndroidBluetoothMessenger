package com.adorastudios.androidbluetoothmessenger.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothController
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothDeviceData.Companion.toData
import com.adorastudios.androidbluetoothmessenger.domain.ConnectionResult
import com.adorastudios.androidbluetoothmessenger.domain.Message
import com.adorastudios.androidbluetoothmessenger.domain.MessageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.lang.Exception
import java.util.UUID

class BluetoothControllerImpl(private val context: Context) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

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

    private var transferService: BluetoothMessageTransferService? = null

    @SuppressLint("MissingPermission")
    private val connectionChangeReceiver =
        ConnectionChangeReceiver { isConnected, bluetoothDevice ->
            runIfHasConnectPermission {
                if (bluetoothAdapter.bondedDevices?.contains(bluetoothDevice) == true) {
                    _isConnected.update { isConnected }
                }
            }
        }

    init {
        updatePairedDevices()
        context.registerReceiver(
            connectionChangeReceiver,
            IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            },
        )
    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery() = runIfHasScanPermission {
        context.registerReceiver(
            newDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND),
        )

        updatePairedDevices()

        bluetoothAdapter.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopDiscovery() = runIfHasScanPermission {
        bluetoothAdapter.cancelDiscovery()
    }

    private var currentServerSocket: BluetoothServerSocket? = null
    private var currentClientSocket: BluetoothSocket? = null

    @SuppressLint("MissingPermission")
    override fun startService(device: BluetoothDeviceData): Flow<ConnectionResult> {
        return flow {
            runIfHasConnectPermission {
                currentServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    "bluetoothMessenger_service",
                    UUID.fromString(SERVICE_UUID),
                )
                var shouldLoop = true
                while (shouldLoop) {
                    val newClientSocket = try {
                        currentServerSocket?.accept()
                    } catch (e: IOException) {
                        shouldLoop = false
                        null
                    }
                    if (newClientSocket?.remoteDevice?.address == device.address) {
                        currentClientSocket = newClientSocket

                        emit(ConnectionResult.Connected)
                        currentClientSocket?.let {
                            currentServerSocket?.close()
                            val service = BluetoothMessageTransferService(
                                newClientSocket,
                                newClientSocket.remoteDevice.name,
                            )
                            transferService = service
                            emitAll(service.listenForIncomingMessages())
                        }
                    }
                }
            }
        }.onCompletion { closeConnection() }
            .flowOn(Dispatchers.IO)
    }

    @SuppressLint("MissingPermission")
    override fun connectToDevice(device: BluetoothDeviceData): Flow<ConnectionResult> {
        return flow {
            runIfHasConnectPermission {
                currentClientSocket = bluetoothAdapter
                    ?.getRemoteDevice(device.address)
                    ?.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
                stopDiscovery()

                currentClientSocket?.let {
                    try {
                        it.connect()
                        emit(ConnectionResult.Connected)

                        val service = BluetoothMessageTransferService(it, it.remoteDevice.name)
                        transferService = service
                        emitAll(service.listenForIncomingMessages())
                    } catch (e: Exception) {
                        it.close()
                        currentClientSocket = null
                        emit(ConnectionResult.Error("Connection was interrupted"))
                    }
                }
            }
        }.onCompletion { closeConnection() }
            .flowOn(Dispatchers.IO)
    }

    override fun closeConnection() {
        currentServerSocket?.close()
        currentServerSocket = null
        currentClientSocket?.close()
        currentClientSocket = null
    }

    @SuppressLint("MissingPermission")
    override suspend fun sendText(text: String): Message? {
        runIfHasConnectPermission {
            if (transferService == null) {
                return null
            }

            val data = MessageData(text = text)
            transferService?.sendMessage(data)

            return Message(
                data = data,
                sender = bluetoothAdapter?.name,
                isLocal = true,
            )
        }
        return null
    }

    override fun release() {
        context.unregisterReceiver(newDeviceReceiver)
        context.unregisterReceiver(connectionChangeReceiver)
        closeConnection()
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

    companion object {
        private const val SERVICE_UUID = "5d2a33a9-5909-4876-b630-fa27c403de86"
    }
}
