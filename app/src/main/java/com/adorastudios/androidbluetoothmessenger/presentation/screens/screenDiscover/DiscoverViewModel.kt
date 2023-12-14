package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothController
import com.adorastudios.androidbluetoothmessenger.domain.ConnectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
) : ViewModel() {
    private val _state: MutableStateFlow<DiscoverState> = MutableStateFlow(DiscoverState())
    val state = combine(
        _state,
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
    ) { state, scannedDevices, pairedDevices ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    private var connectJob: Job? = null

    init {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { state ->
                state.copy(isConnected = isConnected)
            }
        }
    }

    fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.StartScan -> {
                bluetoothController.startDiscovery()
            }

            DiscoverEvent.StopScan -> {
                bluetoothController.stopDiscovery()
            }

            is DiscoverEvent.ConnectToClient -> {
                _state.update {
                    it.copy(
                        isConnecting = true,
                        server = true,
                    )
                }
                connectJob = bluetoothController
                    .startService(event.data)
                    .listen()
            }

            is DiscoverEvent.ConnectToServer -> {
                _state.update {
                    it.copy(
                        isConnecting = true,
                        server = false,
                    )
                }
                connectJob = bluetoothController
                    .connectToDevice(event.data)
                    .listen()
            }

            DiscoverEvent.StopConnection -> {
                bluetoothController.closeConnection()
                connectJob?.cancel()
                _state.update {
                    it.copy(
                        isConnecting = false,
                    )
                }
            }
        }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.Connected -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                        )
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                        )
                    }
                }
            }
        }
            .catch { _ ->
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}
