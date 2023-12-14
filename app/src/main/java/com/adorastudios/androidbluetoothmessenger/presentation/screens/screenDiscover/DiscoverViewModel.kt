package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adorastudios.androidbluetoothmessenger.domain.BluetoothController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.StartScan -> {
                bluetoothController.startDiscovery()
            }
            DiscoverEvent.StopScan -> {
                bluetoothController.stopDiscovery()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
