package com.adorastudios.androidbluetoothmessenger.domain

sealed class ConnectionResult {
    object Connected : ConnectionResult()
    data class Success(val message: Message) : ConnectionResult()
    data class Error(val message: String) : ConnectionResult()
}
