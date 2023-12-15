package com.adorastudios.androidbluetoothmessenger.domain

data class Message(
    val data: MessageData,
    val sender: String?,
    val isLocal: Boolean,
)
