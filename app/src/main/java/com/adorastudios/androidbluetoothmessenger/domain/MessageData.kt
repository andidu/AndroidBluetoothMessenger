package com.adorastudios.androidbluetoothmessenger.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageData(
    @SerialName("text")
    val text: String,
)
