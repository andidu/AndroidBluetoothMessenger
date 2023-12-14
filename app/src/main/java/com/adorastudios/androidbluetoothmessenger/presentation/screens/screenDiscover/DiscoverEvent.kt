package com.adorastudios.androidbluetoothmessenger.presentation.screens.screenDiscover

sealed class DiscoverEvent {
    object StartScan : DiscoverEvent()
    object StopScan : DiscoverEvent()
}
